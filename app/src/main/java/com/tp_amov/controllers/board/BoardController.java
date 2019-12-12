package com.tp_amov.controllers.board;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tp_amov.events.board.BoardEvents;
import com.tp_amov.models.board.Board;
import com.tp_amov.models.board.BoardPosition;
import com.tp_amov.models.board.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BoardController extends ViewModel
{
//Enums
    private enum NetworkRequestType {
        insertNumber,
        validateSolution,
        requestHint
    }
//Control vars
    private Board board;
    private Integer hintsLeft = 3;
    private String difficulty;
    private boolean alreadyCreated = false;
     private BoardPosition numberInfo;
//Callbacks
    private BoardEvents boardEvents;
//Network vars
    private RequestQueue queue;
    private final String WEBSERVICE_URL ="https://sugoku.herokuapp.com/";

//------------> Initializers

    public BoardController(Context context, String difficulty, BoardEvents boardEvents) {
        this.boardEvents = boardEvents;
        this.queue = Volley.newRequestQueue(context);
        this.difficulty = difficulty;
    }

    public void InitializeBoard(){
        if(alreadyCreated)
            boardEvents.getOnBoardCreationSuccess().accept(board.toArray());
        else{
            GetOnlineBoard(difficulty);
            alreadyCreated = true;
        }
    }

//------------> Network

    private void GetOnlineBoard(String difficulty) {
        String urlGET = WEBSERVICE_URL + "board?difficulty=" + difficulty;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGET, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        board = new Board(response);
//                        board = new Board(new JSONObject("{\"board\": [[0,0,1,0,0,0,0,0,0], [2,0,0,0,0,0,0,7,0], [0,7,0,0,0,0,0,0,0], [1,0,0,4,0,6,0,0,7], [0,0,0,0,0,0,0,0,0], [0,0,0,0,1,2,5,4,6], [3,0,2,7,6,0,9,8,0], [0,6,4,9,0,3,0,0,1], [9,8,0,5,2,1,0,6,0]]}"));
                        boardEvents.getOnBoardCreationSuccess().accept(board.toArray(Element.Type.defaultValue));
                    } catch (JSONException e) {
                        boardEvents.getOnBoardCreationError().run();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    boardEvents.getOnBoardCreationError().run();
                }
            });

        queue.add(jsonObjectRequest);
    }

    private void GetOnlineSolvedBoard(final NetworkRequestType networkRequestType){

        String urlPOST = WEBSERVICE_URL + "solve";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            switch (networkRequestType){
                                case insertNumber: InsertNumberResponse(resp); break;
                                case validateSolution: ValidateSolutionResponse(resp); break;
                                case requestHint: RequestHintResponse(resp); break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String URLEncodedBoard = board.toURLEncodedString();
                if(URLEncodedBoard != null)
                    return URLEncodedBoard.getBytes();
                return "".getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        //set  timeout policy
        int socketTimeout = 300000;//300 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    private void InsertNumberResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            if(resp.equals("unsolvable") || resp.equals("broken")) {
                board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(),0);
                boardEvents.getOnInsertInvalidNumber().run();
            }
        } catch (JSONException e) {
            boardEvents.getOnInsertInvalidNumber().run();
        }
    }

    private void ValidateSolutionResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            if(resp.equals("solved"))
                boardEvents.getOnBoardSolved().run();
            else
                boardEvents.getOnBoardUnsolved().run();
        } catch (JSONException e) {
            boardEvents.getOnBoardUnsolved().run();
        }
    }

    private boolean CellIsWritable(BoardPosition boardPosition){
        Integer innerBoardIndex = boardPosition.GetInnerBoardIndex();
        Integer cellIndex = boardPosition.GetCellIndex();
        Element element = board.GetInnerBoard(innerBoardIndex).GetElement(cellIndex);
        return element.GetType() == Element.Type.userValue && element.GetValue() == 0;
    }

    private BoardPosition GetHintFromSolvedBoard(JSONArray jsonBoard) throws JSONException {
        BoardPosition hint = new BoardPosition();

        int totalInnerBoards = board.GetInnerBoards().size();
        int innerBoardsSize = board.GetInnerBoards().get(totalInnerBoards-1).GetElements().size();
        Random rand = new Random();

        do{
            hint.SetInnerBoardIndex(rand.nextInt(totalInnerBoards));   //RANGE [0, totalInnerBoards - 1]
            hint.SetCellIndex(rand.nextInt(innerBoardsSize));          //RANGE [0, innerBoardsSize - 1]
        } while (!CellIsWritable(hint));

        hint.SetValue(jsonBoard.getJSONArray(hint.GetInnerBoardIndex()).getInt(hint.GetCellIndex()));

        return hint;
    }

    private void RequestHintResponse(JSONObject jsonResp){
        try {
            String status = jsonResp.getString("status");
            hintsLeft--;
            if(status.equals("unsolved")) {
                BoardPosition hint = GetHintFromSolvedBoard(jsonResp.getJSONArray("board"));
                boardEvents.getOnReceivedHint().accept(hint);
            }
            else if(status.equals("unsolvable"))
                boardEvents.getOnBoardUnsolved().run();
        } catch (JSONException e) {
            boardEvents.getOnBoardUnsolved().run();
        }
    }

//------------> Getters

    public ArrayList<Integer> GetValuesFromStartBoard(int index) {
        return board.GetInnerBoard(index).toArray(Element.Type.defaultValue);
    }

//------------> Validations

    public boolean IsCellEditable(int innerBoardIndex, int elementIndex) {
        Element.Type elementType = board.GetInnerBoard(innerBoardIndex).GetElement(elementIndex).GetType();
        return elementType == Element.Type.userValue || elementType == Element.Type.hintValue;
    }

    private boolean IsInvalidNr(int Nr) {
        boardEvents.getOnInsertInvalidNumber().run();
        return false;
    }

//------------> User Interactions

    public void InsertNumber(BoardPosition numberInfo) {
         this.numberInfo = numberInfo;
        board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(),numberInfo.GetValue());
        //TODO: verificar linha e coluna antes de chamar GetOnlineSolvedBoard(...)
        GetOnlineSolvedBoard(NetworkRequestType.insertNumber);
    }

    public void ValidateSolution(){
        GetOnlineSolvedBoard(NetworkRequestType.validateSolution);
    }

    public void RequestHint(){
        if(hintsLeft > 0)
            GetOnlineSolvedBoard(NetworkRequestType.requestHint);
    }


}
