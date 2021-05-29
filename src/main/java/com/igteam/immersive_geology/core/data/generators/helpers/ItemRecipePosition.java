package com.igteam.immersive_geology.core.data.generators.helpers;

public enum ItemRecipePosition {
    LEFT("XOO", false),    //XOO
    CENTER("OXO", false),  //OXO
    RIGHT("OOX", false),   //OOX

    TOP("XOO", true),      //XOO Transposed
    MIDDLE("OXO", true),   //OXO Transposed
    BOTTOM("OOX", true);    //OOX Transposed

    private final String position;
    private final boolean isTransposed;
    ItemRecipePosition(String position, boolean isTransposed){
        this.position = position;
        this.isTransposed = isTransposed;
    }

    public String getPosition() {
        return isTransposed ? transpose(position): position;
    }

    private String transpose(String s){
        return insertPeriodically(s, "\n", 1);
    }

    public String insertPeriodically(
            String text, String insert, int period)
    {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length()/period)+1);

        int index = 0;
        String prefix = "";
        while (index < text.length())
        {
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

}
