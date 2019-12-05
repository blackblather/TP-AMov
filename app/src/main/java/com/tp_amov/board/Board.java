package com.tp_amov.board;

import android.content.Context;
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
        GetOnlineBoard(context, difficulty);
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

    private void GetOnlineBoard(Context context, String difficulty) {
        String urlGET = new String(url + "board?difficulty=" + difficulty);

        queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGET, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        InitBoard(response);
                        boardEvents.onBoardCreationSuccess.accept(GetStartBoardArray());
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

    public void GetOnlineSolvedBoard(){

        String urlPOST = new String(url + "solve");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject resp = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    String body = new String("board=" + URLEncoder.encode("[[1,1,0,0,0,0,8,0,0],[0,0,4,0,0,8,0,0,9],[0,7,0,0,0,0,0,0,5],[0,1,0,0,7,5,0,0,8],[0,5,6,0,9,1,3,0,0],[7,8,0,0,0,0,0,0,0],[0,2,0,0,0,0,0,0,0],[0,0,0,9,3,0,0,1,0],[0,0,5,7,0,0,4,0,3]]","UTF-8"));
                    return body.getBytes();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //------------> Getters

    public InnerBoard getIB(int inner_board_index) {
        return innerBoards.get(inner_board_index);
    }

    public ArrayList<Integer> getValuesFromBoard(int index) {
        return innerBoards.get(index).values;
    }

    public ArrayList<Integer> getValuesFromStartBoard(int index) {
        return startInnerBoards.get(index).values;
    }

    private ArrayList<ArrayList<Integer>> GetStartBoardArray() {
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard i : startInnerBoards)
            boardArray.add(i.GetValues());
        return boardArray;
    }

    public boolean IsCellEditable(int ib_index, int cell) {
        return this.getValuesFromStartBoard(ib_index).get(cell) == 0;
    }

    //------------> Setters

    public void insertNum(Integer innerboard_index,Integer cell_index,Integer value) {
        innerBoards.get(innerboard_index).InsertNum(cell_index,value);
        //TODO: Validation
        boardEvents.onInsertValidNumber.run(innerboard_index, cell_index, value);
    }
}
