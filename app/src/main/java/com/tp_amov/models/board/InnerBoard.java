package com.tp_amov.models.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InnerBoard {
//Control vars
    private ArrayList<Element> elements = new ArrayList<>();

//------------> Initializers

    InnerBoard(ArrayList<Integer> defaultValues) {
        int value;
        for(int i = 0; i < 9; i++) {
            value = defaultValues.get(i);
            this.elements.add(new Element((value == 0 ? Element.Type.userValue : Element.Type.defaultValue), value));
        }
    }
//------------> Validations
    public boolean ContainsValue(int value){
        for (Element e : elements)
            if(e.GetValue() == value)
                return true;
        return false;
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

    public ArrayList<Integer> toArray(Element.Type... types){
        List<Element.Type> typesList = Arrays.asList(types);
        ArrayList<Integer> elementsValues = new ArrayList<>(Collections.nCopies(elements.size(), 0));

        for (int i = 0; i < elements.size(); i++) {
            if(typesList.size() == 0)
                elementsValues.set(i, GetElement(i).GetValue());
            else
                elementsValues.set(i, (typesList.contains(GetElement(i).GetType())?GetElement(i).GetValue():0));
        }
        return elementsValues;
    }

}