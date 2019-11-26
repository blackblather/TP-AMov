package com.tp_amov.board;

import com.tp_amov.RunnableWithObjList;

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
import java.util.Random;

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

        void InsertNum(int cell_index, int value) {
            values.set(cell_index,value);
        }

        ArrayList<Integer> GetValues() {
            return values;
        }
    }

    private ArrayList<InnerBoard> innerBoards = new ArrayList<>();
    private ArrayList<InnerBoard> startInnerBoards = new ArrayList<>();
    private RunnableWithObjList insertNrListner;
    private Runnable invalidNrListener, boardCreationErrorCallback;
    private Consumer<ArrayList<ArrayList<Integer>>> boardCreationSuccessCallback;
    //boardCreationErrorCallback;

    public Board(Context context, String difficulty, Consumer<ArrayList<ArrayList<Integer>>> boardCreationSuccessListener, Runnable boardCreationErrorListener) {
        for (int i = 0; i < 9; i++)
            innerBoards.add(new InnerBoard());
        boardCreationSuccessCallback = boardCreationSuccessListener;
        boardCreationErrorCallback = boardCreationErrorListener;
        GetOnlineBoard(context, difficulty);
    }

    private void InitBoard(JSONObject jsonObject) throws JSONException {
        JSONArray jsonInnerBoards = jsonObject.getJSONArray("board");
        for(int i = 0; i < 9; i++)
            startInnerBoards.add(new InnerBoard(jsonInnerBoards.getJSONArray(i)));
    }

    private ArrayList<ArrayList<Integer>> GetStartBoardArray() {
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard i : startInnerBoards)
            boardArray.add(i.GetValues());
        return boardArray;
    }

    private void GetOnlineBoard(Context context, String difficulty) {
        String url = "https://sugoku.herokuapp.com/board?difficulty=" + difficulty;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        InitBoard(response);
                        boardCreationSuccessCallback.accept(GetStartBoardArray());
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

    public void insertNum(ArrayList<Object> data) {
        insertNrListner.run(data);
        Integer i1 = (Integer)data.get(0);
        Integer i2 = (Integer)data.get(1);
        Integer i3 = (Integer)data.get(2);
        InnerBoard innerBoard = innerBoards.get(i1);
        innerBoard.InsertNum(i2,i3);
    }

    public InnerBoard getIB(int inner_board_index) {
        return innerBoards.get(inner_board_index);
    }

    public ArrayList<Integer> getValuesFromBoard(int index) {
        return innerBoards.get(index).values;
    }

    public ArrayList<Integer> getValuesFromStartBoard(int index) {
        return startInnerBoards.get(index).values;
    }

    public boolean IsCellEditable(int ib_index, int cell) {
        return this.getValuesFromStartBoard(ib_index).get(cell) == 0;
    }

    public void setinsertNrListner (RunnableWithObjList insertNrListner) {
        this.insertNrListner = insertNrListner;
    }

    public void setInvalidNrListener (Runnable invalidNrListener) {
        this.invalidNrListener = invalidNrListener;
    }

    public boolean isInvalidNr(int Nr) {
        invalidNrListener.run();
        return false;
    }
}
