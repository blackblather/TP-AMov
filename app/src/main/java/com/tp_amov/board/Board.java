package com.tp_amov.board;

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

        boolean insertNum(int cell_index, int value)
        {
            values.set(cell_index,value);
            return true;
        }
    }

    private ArrayList<InnerBoard> InnerBoards = new ArrayList<>();
    private Runnable invalidNrListener;

    public Board() {
//        for (int i = 0; i < 9; i++)
//            InnerBoards.add(new InnerBoard());
        //TODO: GET JSON RESPONSE FROM WEBSERVICE
        try{
            String serverResp = "{\"board\":[[0,0,0,0,7,0,8,0,4],[0,2,3,4,0,0,0,0,9],[0,0,8,3,0,0,1,0,5]," +
                                            "[2,1,4,0,3,0,0,9,8],[0,0,0,0,0,0,2,4,0],[0,0,7,0,0,0,0,0,0]," +
                                            "[0,0,0,0,4,0,9,0,2],[7,8,5,9,0,2,4,0,3],[0,0,2,6,8,0,5,0,0]]}";
            JSONObject jsonObject = new JSONObject(serverResp);
            JSONArray jsonInnerBoards = jsonObject.getJSONArray("board");
            for(int i = 0; i < 9; i++)
                InnerBoards.add(new InnerBoard(jsonInnerBoards.getJSONArray(i)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean insertNum(int inner_board_index, int cell_index, int value)
    {
        InnerBoards.get(inner_board_index).insertNum(cell_index,value);
        return true;
    }

    public void setInvalidNrListener (Runnable invalidNrListener) {
        this.invalidNrListener = invalidNrListener;
    }

    public boolean isInvalidNr(int Nr) {
        invalidNrListener.run();
        return false;
    }


}
