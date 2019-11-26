package com.tp_amov.board;

import android.content.Context;
import androidx.core.util.Consumer;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Board
{
    private class InnerBoard {
        //Cria array inicializado com zeros
        private ArrayList<Integer> values = new ArrayList<>(Collections.nCopies(9, 0));

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

//        private void FillBoard(){
//            Random rand = new Random();
//            int val;
//            for (int i = 0; i < 9; i++) {
//                do {
//                    val = rand.nextInt(9) + 1;  //Range: [1 .. 9]
//                } while (values.contains(val));
//                values.set(i, val);
//            }
//        }

        boolean insert(int cell_index, int value)
        {
            values.set(cell_index,value);
            return true;
        }
        ArrayList<Integer> GetArray(){
            return values;
        }
    }

    private ArrayList<InnerBoard> innerBoards = new ArrayList<>();
    private Runnable invalidNrListener, boardCreationErrorCallback;
    private Consumer<ArrayList<ArrayList<Integer>>> boardCreationSuccessCallback;
    //boardCreationErrorCallback;

    public Board(Context context, String difficulty, Consumer<ArrayList<ArrayList<Integer>>> boardCreationSuccessListener, Runnable boardCreationErrorListener) {
        boardCreationSuccessCallback = boardCreationSuccessListener;
        boardCreationErrorCallback = boardCreationErrorListener;
        GetOnlineBoard(context, difficulty);
//        for (int i = 0; i < 9; i++)
//            InnerBoards.add(new InnerBoard());
    }

    private void InitBoard(JSONObject jsonObject) throws JSONException {
        JSONArray jsonInnerBoards = jsonObject.getJSONArray("board");
        for(int i = 0; i < 9; i++)
            innerBoards.add(new InnerBoard(jsonInnerBoards.getJSONArray(i)));
    }

    private ArrayList<ArrayList<Integer>> GetBoardArray(){
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard i : innerBoards)
            boardArray.add(i.GetArray());
        return boardArray;
    }

    private void GetOnlineBoard(Context context, String difficulty){
        String url = "https://sugoku.herokuapp.com/board?difficulty=" + difficulty;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        InitBoard(response);
                        boardCreationSuccessCallback.accept(GetBoardArray());
                    } catch (JSONException e) {
                        boardCreationErrorCallback.run();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    boardCreationErrorCallback.run();
                }
            });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public void start_board() {

    }

    public void insertNumber(int number, int row, int col) {

    }

    public void setInvalidNrListener (Runnable invalidNrListener) {
        this.invalidNrListener = invalidNrListener;
    }

    public boolean isInvalidNr(int Nr) {
        invalidNrListener.run();
        return false;
    }
}
