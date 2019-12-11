package com.tp_amov.models.board;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InnerBoard {
//Control vars
    private ArrayList<Element> elements = new ArrayList<>();

//------------> Initializers

    InnerBoard(JSONArray elements) {
        try{
            int value;
            for(int i = 0; i < 9; i++) {
                value = elements.getInt(i);
                this.elements.add(new Element((value == 0 ? Element.Type.userValue : Element.Type.defaultValue), value));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//------------> Getters

    public ArrayList<Element> GetElements() {
        return elements;
    }

    public Element GetElement(int index){
        return elements.get(index);
    }

//------------> Setters

    public void SetValue(int cell_index, int value) {
        elements.get(cell_index).SetValue(value);
    }

//------------> Converters

    private ArrayList<Integer> toArray(){
        ArrayList<Integer> elementsValues = new ArrayList<>(Collections.nCopies(elements.size(), 0));

        for (int i = 0; i < elements.size(); i++)
            elementsValues.set(i, GetElement(i).GetValue());

        return elementsValues;
    }

    public ArrayList<Integer> toArray(Element.Type... types){
        if(types.length == 0)
            return toArray();
        else{
            List<Element.Type> typesList = Arrays.asList(types);
            ArrayList<Integer> elementsValues = new ArrayList<>(Collections.nCopies(elements.size(), 0));

            for (int i = 0; i < elements.size(); i++) {
                if(typesList.contains(elements.get(i).GetType()))
                    elementsValues.set(i, GetElement(i).GetValue());
            }
            return elementsValues;
        }
    }

}