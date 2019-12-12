package com.tp_amov.models.board;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Board {
//Control vars
    private ArrayList<InnerBoard> innerBoards = new ArrayList<>();

//------------> Initializers

    private InnerBoard getInnerBoardFromJSONArray(JSONArray jsonInnerBoards, int innerBoardIndex) throws JSONException {
        ArrayList<Integer> defaultValues = new ArrayList<>();

        int startRow = (innerBoardIndex / 3)*3; //!!!DIVISION BY INTEGER!!!
        int startCol = (innerBoardIndex % 3)*3;
        for(int row = startRow; row < startRow + 3; row++)
            for(int col = startCol; col < startCol + 3; col++)
                defaultValues.add(jsonInnerBoards.getJSONArray(row).getInt(col));

        return new InnerBoard(defaultValues);
    }

    public Board(JSONObject jsonBoard) throws JSONException {
        JSONArray jsonInnerBoards = jsonBoard.getJSONArray("board");
        for (int i = 0; i < 9; i++)
            innerBoards.add(getInnerBoardFromJSONArray(jsonInnerBoards, i));

    }
//------------> Getters
    public ArrayList<InnerBoard> GetInnerBoards(){
        return innerBoards;
    }

    public InnerBoard GetInnerBoard(int index){
        return innerBoards.get(index);
    }

//------------> Converters

    public String toURLEncodedString(){
        try {
            return "board=" + URLEncoder.encode(toArray(Element.Type.defaultValue, Element.Type.userValue).toString().replace(" ", ""),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private ArrayList<ArrayList<Integer>> toArray(){
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard innerBoard : innerBoards)
            boardArray.add(innerBoard.toArray());
        return boardArray;
    }

    public ArrayList<ArrayList<Integer>> toArray(Element.Type... types){
        if(types.length == 0)
            return toArray();
        else {
            ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
            for (InnerBoard innerBoard : innerBoards)
                boardArray.add(innerBoard.toArray(types));
            return boardArray;
        }
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
