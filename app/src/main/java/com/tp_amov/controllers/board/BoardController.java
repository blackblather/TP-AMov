package com.tp_amov.controllers.board;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.tp_amov.events.board.BoardEvents;
import com.tp_amov.models.board.Board;
import com.tp_amov.models.board.BoardPosition;
import com.tp_amov.models.board.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

public class BoardController extends ViewModel
{
    public static class Factory implements ViewModelProvider.Factory{
        private Context context;
        private String difficulty;
        private BoardEvents boardEvents;
        private boolean useWebservice;

        public Factory(Context context, String difficulty, BoardEvents boardEvents, boolean useWebservice){
            this.context = context;
            this.difficulty = difficulty;
            this.boardEvents = boardEvents;
            this.useWebservice = useWebservice;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Context.class, String.class, BoardEvents.class, Boolean.class).newInstance(context, difficulty, boardEvents, useWebservice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
//Enums
    private enum NetworkRequestType {
        insertNumber,
        validateSolution,
        requestHint
    }
//Control vars
    private Board board;
    private int score = 0;
    private Integer hintsLeft = 3;
    private String difficulty;
    private boolean alreadyCreated = false;
    private boolean useWebservice = false;
    private BoardPosition numberInfo;
//Callbacks
    private BoardEvents boardEvents;
//Network vars
    private RequestQueue queue;
    private final String WEBSERVICE_URL ="https://sugoku.herokuapp.com/";

//------------> Initializers

    static class CustomRequestQueueFactory {
        static RequestQueue NewSingleThreadRequestQueue(Context context) {
            File cacheDir = new File(context.getCacheDir(), "volley");
            RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), new BasicNetwork(new HurlStack()), 1);
            queue.start();
            return queue;
        }
    }

    public BoardController(Context context, String difficulty, BoardEvents boardEvents, Boolean useWebservice) {
        this.boardEvents = boardEvents;
        this.queue = CustomRequestQueueFactory.NewSingleThreadRequestQueue(context);
        this.difficulty = difficulty;
        this.useWebservice = useWebservice;
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
        final String URLEncodedFINAL = board.toURLEncodedString();
        String urlPOST;
        if(networkRequestType == NetworkRequestType.validateSolution)
            urlPOST = WEBSERVICE_URL + "validate";
        else
            urlPOST = WEBSERVICE_URL + "solve";

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
                String URLEncodedBoard = URLEncodedFINAL;
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
                ScoreDecrement();
                boardEvents.getOnInsertInvalidNumber().run();
            } else if (resp.equals("unsolved") || resp.equals("solved")){
                ScoreIncrement();
                boardEvents.getOnInsertValidNumber().run();
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

    private boolean CellIsHintable(BoardPosition boardPosition){
        Integer innerBoardIndex = boardPosition.GetInnerBoardIndex();
        Integer cellIndex = boardPosition.GetCellIndex();
        Element element = board.GetInnerBoard(innerBoardIndex).GetElement(cellIndex);
        return ElementContainsType(innerBoardIndex, cellIndex, Element.Type.userValue) && element.GetValue() == 0;
    }

    private BoardPosition GetHintFromSolvedBoard(JSONArray jsonBoard) throws JSONException {
        BoardPosition hint = new BoardPosition();

        int totalInnerBoards = board.GetInnerBoards().size();
        int innerBoardsSize = board.GetInnerBoards().get(totalInnerBoards-1).GetElements().size();
        Random rand = new Random();

        do{
            hint.SetInnerBoardIndex(rand.nextInt(totalInnerBoards));   //RANGE [0, totalInnerBoards - 1]
            hint.SetCellIndex(rand.nextInt(innerBoardsSize));          //RANGE [0, innerBoardsSize - 1]
        } while (!CellIsHintable(hint));

        hint.SetValue(jsonBoard.getJSONArray(hint.GetInnerBoardIndex()).getInt(hint.GetCellIndex()));

        return hint;
    }

    private void RequestHintResponse(JSONObject jsonResp){
        try {
            String status = jsonResp.getString("status");
            if(status.equals("solved")) {
                BoardPosition hint = GetHintFromSolvedBoard(jsonResp.getJSONArray("solution"));
                hintsLeft--;
                board.GetInnerBoard(hint.GetInnerBoardIndex()).SetValue(hint.GetCellIndex(),hint.GetValue());
                board.GetInnerBoard(hint.GetInnerBoardIndex()).GetElement(hint.GetCellIndex()).SetType(Element.Type.hintValue);
                boardEvents.getOnReceivedHint().accept(hint);
            }
            else if(status.equals("unsolvable"))
                boardEvents.getOnBoardUnsolved().run();
        } catch (JSONException e) {
            boardEvents.getOnBoardUnsolved().run();
        }
    }

//------------> Getters

    public ArrayList<Integer> GetValuesFromStartBoard(int innerBoardIndex) {
        return board.GetInnerBoard(innerBoardIndex).toArray(Element.Type.defaultValue);
    }

    public Element.Type GetElementType(int innerBoardIndex, int elementIndex){
        return board.GetInnerBoard(innerBoardIndex).GetElement(elementIndex).GetType();
    }

//------------> Validations

    public boolean ElementContainsType(int innerBoardIndex, int elementIndex, Element.Type... types) {
        List<Element.Type> typesList = Arrays.asList(types);
        Element.Type elementType = board.GetInnerBoard(innerBoardIndex).GetElement(elementIndex).GetType();
        return typesList.contains(elementType);
    }

    private boolean IsValidNumber(BoardPosition numberInfo) throws JSONException {
        //Checks if innerboard contains value
        if(!board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).ContainsValue(numberInfo.GetValue())){
            //Gets base and offsets to calculate row and col were the value should be placed
            int baseRow = (numberInfo.GetInnerBoardIndex()/3)*3,    //Used as a base to calculate row
                baseColumn = (numberInfo.GetInnerBoardIndex()%3)*3, //Used as a base to calculate row
                offsetRow = numberInfo.GetCellIndex()/3,            //Add to base to get row
                offsetColumn = numberInfo.GetCellIndex()%3;         //Add to base to get column
            //Calculate row and columns
            int row = baseRow + offsetRow,
                column = baseColumn + offsetColumn;
            //Get board in JSONArray (NOT USING INNERBOARDS) format.
            //WARNING: This step trades time complexity for less code complexity
            JSONArray boardArray = board.toJSONArray();
            //Check for duplicates in (i, column) and (row, i) in the same "for" loop
            for(int i = 0; i < 9; i++){
                if(boardArray.getJSONArray(i).getInt(column) == numberInfo.GetValue() && i != row)
                    return false;
                if(boardArray.getJSONArray(row).getInt(i) == numberInfo.GetValue() && i != column)
                    return false;
            }
            return true;
        }
        return false;
    }

//------------> User Interactions

    public void InsertNumber(BoardPosition numberInfo) {
        if(useWebservice) {
            this.numberInfo = numberInfo;
            board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(),numberInfo.GetValue());
            GetOnlineSolvedBoard(NetworkRequestType.insertNumber);
        } else {
            try {
                board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(), 0);
                if (IsValidNumber(numberInfo)) {
                    board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(), numberInfo.GetValue());
                    if(numberInfo.GetValue() != 0) ScoreIncrement();
                    boardEvents.getOnInsertValidNumber().run();
                } else {
                    ScoreDecrement();
                    boardEvents.getOnInsertInvalidNumber().run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void ScoreIncrement(){
        score += 5;
    }

    private void ScoreDecrement(){
        score -= 3;
    }

    public Integer getScore(){
        return score;
    }

    public void ValidateSolution(){
        GetOnlineSolvedBoard(NetworkRequestType.validateSolution);
    }

    public void RequestHint(){
        if(hintsLeft > 0)
            GetOnlineSolvedBoard(NetworkRequestType.requestHint);
    }


}
