package com.tp_amov.board;

import android.content.Context;
import androidx.annotation.NonNull;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class Board
{
    private enum RequestType{
        insertNumber,
        validateSolution
    }
    private class InnerBoard {
        //Cria array inicializado com zeros
        private ArrayList<Integer> values = new ArrayList<>(Collections.nCopies(9, 0));

        //------------> Initializers
        InnerBoard() {
//            FillBoard();
        }
        InnerBoard(JSONArray elements) {
            try{
                for(int i = 0; i < 9; i++)
                    values.set(i, elements.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void FillBoard(){
            Random rand = new Random();
            int val;
            for (int i = 0; i < 9; i++) {
                do {
                    val = rand.nextInt(9) + 1;  //Range: [1 .. 9]
                } while (values.contains(val));
                values.set(i, val);
            }
        }

        //------------> Getters

        ArrayList<Integer> GetValues() {
            return values;
        }

        //------------> Setters

        void InsertNum(int cell_index, int value) {
            values.set(cell_index,value);
        }

    }

    //Inner boards
    private ArrayList<InnerBoard> innerBoards = new ArrayList<>();
    private ArrayList<InnerBoard> startInnerBoards = new ArrayList<>();
    //Callbacks
    private BoardEvents boardEvents;
    //boardCreationErrorCallback;
    //Network vars
    private RequestQueue queue;
    private final String url ="https://sugoku.herokuapp.com/";

    //------------> Initializers

    public Board(Context context, String difficulty, BoardEvents boardEvents) {
        for (int i = 0; i < 9; i++)
            innerBoards.add(new InnerBoard());
        this.boardEvents = boardEvents;
        this.queue = Volley.newRequestQueue(context);
        GetOnlineBoard(difficulty);
    }

    private void InitBoard(JSONObject jsonObject) throws JSONException {
        JSONArray jsonInnerBoards = jsonObject.getJSONArray("board");
        for (int i = 0; i < 9; i++)
            startInnerBoards.add(new InnerBoard(jsonInnerBoards.getJSONArray(i)));
    }

    //------------> Private functions

    private boolean isInvalidNr(int Nr) {
        boardEvents.onInsertInvalidNumber.run();
        return false;
    }

    //------------> Network

    private void GetOnlineBoard(String difficulty) {
        String urlGET = new String(url + "board?difficulty=" + difficulty);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGET, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        InitBoard(response);
                        boardEvents.onBoardCreationSuccess.accept(toArray());
                    } catch (JSONException e) {
                        boardEvents.onBoardCreationError.run();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    boardEvents.onBoardCreationError.run();
                }
            });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    private void GetOnlineSolvedBoard(final RequestType requestType){

        String urlPOST = new String(url + "solve");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject resp = new JSONObject(response);
                            switch (requestType){
                                case insertNumber: InsertNumberResponse(resp); break;
                                case validateSolution: ValidateResponse(resp); break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String URLEncodedBoard = toURLEncodedString();
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

        queue.add(stringRequest);
    }

    private void InsertNumberResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            if(resp.equals("unsolved") || resp.equals("solved"))
                boardEvents.onInsertValidNumber.run();
            else
                boardEvents.onInsertInvalidNumber.run();
        } catch (JSONException e) {
            boardEvents.onInsertInvalidNumber.run();
        }
    }

    private void ValidateResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            if(resp.equals("solved"))
                boardEvents.onBoardSolved.run();
            else
                boardEvents.onBoardUnsolved.run();
        } catch (JSONException e) {
            boardEvents.onBoardUnsolved.run();
        }
    }

    //------------> Getters

    public ArrayList<Integer> getValuesFromStartBoard(int index) {
        return startInnerBoards.get(index).values;
    }

    public boolean IsCellEditable(int ib_index, int cell) {
        return this.getValuesFromStartBoard(ib_index).get(cell) == 0;
    }

    //------------> Validations

    public void InsertNumber(Integer innerboard_index, Integer cell_index, Integer value) {
        innerBoards.get(innerboard_index).InsertNum(cell_index,value);
        GetOnlineSolvedBoard(RequestType.insertNumber);
    }

    public void ValidateSolution(){
        GetOnlineSolvedBoard(RequestType.validateSolution);
    }

    //------------> Converters

    @NonNull
    @Override
    public String toString(){
        return toJSONObject().toString();
    }

    private String toURLEncodedString(){
        try {
            return new String("board=" + URLEncoder.encode(toArray().toString().replace(" ", ""),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private ArrayList<ArrayList<Integer>> toArray(){
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard i : startInnerBoards)
            boardArray.add(i.GetValues());
        return boardArray;
    }

    private JSONObject toJSONObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("board", toArray());
            return jsonObject;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
}
