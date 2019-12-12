package com.tp_amov.models.board;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
//Control vars
    private ArrayList<InnerBoard> innerBoards = new ArrayList<>();

//------------> Initializers

    public Board(JSONObject jsonBoard) throws JSONException {
        JSONArray jsonInnerBoards = jsonBoard.getJSONArray("board");
        for (int i = 0; i < 9; i++)
            innerBoards.add(InnerBoardFromJSONArray(jsonInnerBoards, i));
        String tmp = toJSONArray().toString();
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
            return "board=" + URLEncoder.encode(toJSONArray(Element.Type.defaultValue, Element.Type.userValue).toString().replace(" ", ""),"UTF-8");
        } catch (UnsupportedEncodingException | JSONException e) {
            return null;
        }
    }

    public ArrayList<ArrayList<Integer>> toArray(Element.Type... types){
        ArrayList<ArrayList<Integer>> boardArray = new ArrayList<>();
        for (InnerBoard innerBoard : innerBoards)
            boardArray.add(innerBoard.toArray(types));
        return boardArray;
    }

    private InnerBoard InnerBoardFromJSONArray(JSONArray jsonInnerBoards, int innerBoardIndex) throws JSONException {
        ArrayList<Integer> defaultValues = new ArrayList<>();

        int startRow = (innerBoardIndex / 3)*3; //!!!DIVISION BY INTEGER!!!
        int startCol = (innerBoardIndex % 3)*3;
        for(int row = startRow; row < startRow + 3; row++)
            for(int col = startCol; col < startCol + 3; col++)
                defaultValues.add(jsonInnerBoards.getJSONArray(row).getInt(col));

        return new InnerBoard(defaultValues);
    }

    private JSONArray JSONArrayRowFromInnerBoards(int rowIndex, Element.Type... types) throws JSONException {
        JSONArray jsonArrayRow = new JSONArray();
        List<Element.Type> typesList = Arrays.asList(types);

        int startInnerBoardIndex = (rowIndex / 3)*3; //!!!DIVISION BY INTEGER!!!
        int startElementIndex = (rowIndex % 3)*3;
        for(int innerBoardIndex = startInnerBoardIndex; innerBoardIndex < startInnerBoardIndex + 3; innerBoardIndex++)
            for(int elementIndex = startElementIndex; elementIndex < startElementIndex + 3; elementIndex++) {
                if(typesList.size() == 0)
                    jsonArrayRow.put(innerBoards.get(innerBoardIndex).GetElement(elementIndex).GetValue());
                else {
                    Element element = innerBoards.get(innerBoardIndex).GetElement(elementIndex);
                    jsonArrayRow.put(typesList.contains(element.GetType())?element.GetValue():0);
                }
            }
        return jsonArrayRow;
    }

    private JSONArray toJSONArray(Element.Type... types) throws JSONException {
        JSONArray jsonInnerBoards = new JSONArray();
        for (int row = 0; row < 9; row++)
            jsonInnerBoards.put((Object) JSONArrayRowFromInnerBoards(row, types));
        return jsonInnerBoards;
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
