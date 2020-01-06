package com.tp_amov.controllers.game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.tp_amov.SelectUserFragmentM1;
import com.tp_amov.controllers.sql.GameModeController;
import com.tp_amov.controllers.sql.UserGameController;
import com.tp_amov.events.game.GameEvents;
import com.tp_amov.models.board.Board;
import com.tp_amov.models.board.BoardPosition;
import com.tp_amov.models.board.Element;
import com.tp_amov.models.sql.Game;
import com.tp_amov.models.sql.GameMode;
import com.tp_amov.models.sql.User;
import com.tp_amov.models.sql.UserGame;
import com.tp_amov.tools.SudokuDbHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

public class GameController extends ViewModel
{
    //Factory
    public static class Factory implements ViewModelProvider.Factory{
        private Context context;
        private String difficulty;
        private String mode;
        private ArrayList<User> users;
        private GameEvents gameEvents;
        private boolean useWebservice;

        public Factory(Context context, String difficulty, String mode, ArrayList<User> users, GameEvents gameEvents, boolean useWebservice){
            this.context = context;
            this.difficulty = difficulty;
            this.mode = mode;
            this.users = users;
            this.gameEvents = gameEvents;
            this.useWebservice = useWebservice;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Context.class, String.class, String.class, ArrayList.class, GameEvents.class, Boolean.class).newInstance(context, difficulty, mode, users, gameEvents, useWebservice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Enum
    private enum NetworkRequestType {
        insertNumber,
        validateSolution,
        requestHint
    }
    //Control vars
    private Board board;
    private int score = 0;
    private Integer hintsLeft = 3;
    private ArrayList<User> users;
    //"requestStack" is used to store requests made by the user (eg. insert number) and to synchronize webservice responses with user requests
    private LinkedList<BoardPosition> requestStack = new LinkedList<>();
    private Context context;
    private String difficulty;
    private String mode;
    private boolean alreadyCreated = false;
    private boolean useWebservice = false;
    //Callbacks
    private GameEvents gameEvents;
    //Network vars
    private RequestQueue queue;
    private final String WEBSERVICE_URL ="https://sugoku.herokuapp.com/";

//------------> Initializers

    static class CustomRequestQueueFactory {
        static RequestQueue NewSingleThreadRequestQueue(Context context) {
            File cacheDir = new File(context.getCacheDir(), "volley");
            RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), new BasicNetwork(new HurlStack()), 1);
            queue.start();
            return queue;
        }
    }

    public GameController(Context context, String difficulty, String mode, ArrayList<User> users, GameEvents gameEvents, Boolean useWebservice) {
        this.context = context;
        this.queue = CustomRequestQueueFactory.NewSingleThreadRequestQueue(context);
        this.difficulty = difficulty;
        this.mode = mode;
        this.users = users;
        this.gameEvents = gameEvents;
        this.useWebservice = useWebservice;
    }

    public void InitializeBoard(){
        if(alreadyCreated)
            gameEvents.getOnBoardCreationSuccess().accept(board.toArray());
        else{
            GetOnlineBoard(difficulty);
            alreadyCreated = true;
        }
    }

//------------> Network

    private void GetOnlineBoard(String difficulty) {
        String urlGET = WEBSERVICE_URL + "board?difficulty=" + difficulty;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlGET, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        //board = new Board(response);
                        //UNSOLVED
                        //board = new Board(new JSONObject("{\"board\": [[0,0,1,0,0,0,0,0,0], [2,0,0,0,0,0,0,7,0], [0,7,0,0,0,0,0,0,0], [1,0,0,4,0,6,0,0,7], [0,0,0,0,0,0,0,0,0], [0,0,0,0,1,2,5,4,6], [3,0,2,7,6,0,9,8,0], [0,6,4,9,0,3,0,0,1], [9,8,0,5,2,1,0,6,0]]}"));
                        //SOLVED
                        board = new Board(new JSONObject("{\"board\": [[9,8,4,5,7,2,6,1,3], [1,2,3,4,6,8,5,7,9], [5,6,7,1,3,9,2,4,8], [2,1,5,3,4,6,8,9,7], [3,4,6,8,9,7,1,2,5], [7,9,8,2,1,5,3,6,4], [4,3,2,7,5,1,9,8,6], [6,5,1,9,8,4,7,3,2], [8,7,9,6,2,3,4,5,1]]}"));
                        gameEvents.getOnBoardCreationSuccess().accept(board.toArray(Element.Type.defaultValue));
                    } catch (JSONException e) {
                        gameEvents.getOnBoardCreationError().run();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    gameEvents.getOnBoardCreationError().run();
                }
            });

        queue.add(jsonObjectRequest);
    }

    private void GetOnlineSolvedBoard(final NetworkRequestType networkRequestType){
        final String URLEncodedFINAL = board.toURLEncodedString();
        String urlPOST;
        if(networkRequestType == NetworkRequestType.validateSolution)
            urlPOST = WEBSERVICE_URL + "validate";
        else
            urlPOST = WEBSERVICE_URL + "solve";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlPOST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            switch (networkRequestType){
                                case insertNumber: InsertNumberResponse(resp); break;
                                case validateSolution: ValidateSolutionResponse(resp); break;
                                case requestHint: RequestHintResponse(resp); break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String URLEncodedBoard = URLEncodedFINAL;
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

        //set  timeout policy
        int socketTimeout = 300000;//300 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    private void InsertNumberResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            BoardPosition request = requestStack.removeFirst();
            if(resp.equals("unsolvable") || resp.equals("broken")) {
                if(!request.GetValue().equals(request.GetOldValue()))
                    ScoreDecrement();
                board.GetInnerBoard(request.GetInnerBoardIndex()).SetValue(request.GetCellIndex(),0);
                gameEvents.getOnInsertInvalidNumber().run();
            } else if (resp.equals("unsolved") || resp.equals("solved")){
                if(!request.GetValue().equals(request.GetOldValue()) && !request.GetValue().equals(0))
                    ScoreIncrement();
                else
                    ScoreDecrement();
                gameEvents.getOnInsertValidNumber().run();
            }
        } catch (JSONException e) {
            gameEvents.getOnInsertInvalidNumber().run();
        }
    }

    private void SaveGameResult(){
        //Initializing
        SudokuDbHelper dbHelper = new SudokuDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (mode){
            //Game mode M1
            case SelectUserFragmentM1.GAME_MODE: {
                //Get game mode
                GameModeController gameModeController = new GameModeController(db, dbHelper);
                GameMode gameMode = gameModeController.GetGameMode(SelectUserFragmentM1.GAME_MODE);

                //Add new game. Warning: USING FULL CLASS NAME (package + class name) TO AVOID AMBIGUITY!!! AAAAAAAAAAAAAAAAAAAAAAAAH
                com.tp_amov.controllers.sql.GameController gameController = new com.tp_amov.controllers.sql.GameController(db, dbHelper);
                Game game = new Game(gameMode);
                gameController.AddGame(game);

                //Associate user with game
                UserGameController userGameController = new UserGameController(db, dbHelper);
                UserGame userGame = new UserGame(users.get(0), game, score);
                userGameController.AddUserGame(userGame);
            } break;
        }


        //Close database connection
        db.close();
        dbHelper.close();
    }

    private void ValidateSolutionResponse(JSONObject jsonResp){
        try {
            String resp = jsonResp.getString("status");
            if(resp.equals("solved")) {
                SaveGameResult();
                gameEvents.getOnBoardSolved().run();
            } else
                gameEvents.getOnBoardUnsolved().run();
        } catch (JSONException e) {
            gameEvents.getOnBoardUnsolved().run();
        }
    }

    private boolean CellIsHintable(BoardPosition boardPosition){
        Integer innerBoardIndex = boardPosition.GetInnerBoardIndex();
        Integer cellIndex = boardPosition.GetCellIndex();
        Element element = board.GetInnerBoard(innerBoardIndex).GetElement(cellIndex);
        return ElementContainsType(innerBoardIndex, cellIndex, Element.Type.userValue) && element.GetValue() == 0;
    }

    private BoardPosition GetHintFromSolvedBoard(JSONArray jsonBoard) throws JSONException {
        BoardPosition hint = new BoardPosition();

        int totalInnerBoards = board.GetInnerBoards().size();
        int innerBoardsSize = board.GetInnerBoards().get(totalInnerBoards-1).GetElements().size();
        Random rand = new Random();

        do {
            hint.SetInnerBoardIndex(rand.nextInt(totalInnerBoards));   //RANGE [0, totalInnerBoards - 1]
            hint.SetCellIndex(rand.nextInt(innerBoardsSize));          //RANGE [0, innerBoardsSize - 1]
        } while (!CellIsHintable(hint));

        //Cada sub-array em jsonBoard representa uma linha da board (não uma inner board)
        int rowOffset = (hint.GetCellIndex() / 3);
        int row = ((hint.GetInnerBoardIndex() / 3) * 3) + rowOffset;
        int colOffset = (hint.GetCellIndex() % 3);
        int col = ((hint.GetInnerBoardIndex() % 3) * 3) + colOffset;

        hint.SetValue(jsonBoard.getJSONArray(row).getInt(col));

        return hint;
    }

    private void RequestHintResponse(JSONObject jsonResp){
        try {
            String status = jsonResp.getString("status");
            if(status.equals("solved")) {
                BoardPosition hint = GetHintFromSolvedBoard(jsonResp.getJSONArray("solution"));
                hintsLeft--;
                board.GetInnerBoard(hint.GetInnerBoardIndex()).SetValue(hint.GetCellIndex(),hint.GetValue());
                board.GetInnerBoard(hint.GetInnerBoardIndex()).GetElement(hint.GetCellIndex()).SetType(Element.Type.hintValue);
                gameEvents.getOnReceivedHint().accept(hint);
            }
            else if(status.equals("unsolvable"))
                gameEvents.getOnBoardUnsolved().run();
        } catch (JSONException e) {
            gameEvents.getOnBoardUnsolved().run();
        }
    }

//------------> Getters

    public ArrayList<Integer> GetValuesFromStartBoard(int innerBoardIndex) {
        return board.GetInnerBoard(innerBoardIndex).toArray(Element.Type.defaultValue);
    }

    public Element.Type GetElementType(int innerBoardIndex, int elementIndex){
        return board.GetInnerBoard(innerBoardIndex).GetElement(elementIndex).GetType();
    }

    public Integer getScore(){
        return score;
    }

//------------> Setters

    private void ScoreIncrement(){
        score += 5;
    }

    private void ScoreDecrement(){
        score -= 5;
    }

//------------> Validations

    public boolean ElementContainsType(int innerBoardIndex, int elementIndex, Element.Type... types) {
        List<Element.Type> typesList = Arrays.asList(types);
        Element.Type elementType = board.GetInnerBoard(innerBoardIndex).GetElement(elementIndex).GetType();
        return typesList.contains(elementType);
    }

    private boolean IsValidNumber(BoardPosition numberInfo) throws JSONException {
        //Checks if innerboard contains value
        if(!board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).ContainsValue(numberInfo.GetValue())){
            //Gets base and offsets to calculate row and col were the value should be placed
            int baseRow = (numberInfo.GetInnerBoardIndex()/3)*3,    //Used as a base to calculate row
                baseColumn = (numberInfo.GetInnerBoardIndex()%3)*3, //Used as a base to calculate row
                offsetRow = numberInfo.GetCellIndex()/3,            //Add to base to get row
                offsetColumn = numberInfo.GetCellIndex()%3;         //Add to base to get column
            //Calculate row and columns
            int row = baseRow + offsetRow,
                column = baseColumn + offsetColumn;
            //Get board in JSONArray (NOT USING INNERBOARDS) format.
            //WARNING: This step trades time complexity for less code complexity
            JSONArray boardArray = board.toJSONArray();
            //Check for duplicates in (i, column) and (row, i) in the same "for" loop
            for(int i = 0; i < 9; i++){
                if(boardArray.getJSONArray(i).getInt(column) == numberInfo.GetValue() && i != row)
                    return false;
                if(boardArray.getJSONArray(row).getInt(i) == numberInfo.GetValue() && i != column)
                    return false;
            }
            return true;
        }
        return false;
    }

//------------> User Interactions

    public void InsertNumber(BoardPosition numberInfo) {
        int oldValue = board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).GetElement(numberInfo.GetCellIndex()).GetValue();
        if (useWebservice) {
            numberInfo.SetOldValue(oldValue);
            requestStack.add(numberInfo);
            board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).GetElement(numberInfo.GetCellIndex()).SetType(Element.Type.userValue);
            board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(), numberInfo.GetValue());
            GetOnlineSolvedBoard(NetworkRequestType.insertNumber);
        } else {
            try {
                board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(), 0);
                if (IsValidNumber(numberInfo)) {
                    if (!numberInfo.GetValue().equals(0) && !numberInfo.GetValue().equals(oldValue))
                        ScoreIncrement();
                    board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).SetValue(numberInfo.GetCellIndex(), numberInfo.GetValue());
                    board.GetInnerBoard(numberInfo.GetInnerBoardIndex()).GetElement(numberInfo.GetCellIndex()).SetType(Element.Type.userValue);
                    gameEvents.getOnInsertValidNumber().run();
                } else {
                    if (!numberInfo.GetValue().equals(oldValue))
                        ScoreDecrement();
                    gameEvents.getOnInsertInvalidNumber().run();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void ValidateSolution(){
        GetOnlineSolvedBoard(NetworkRequestType.validateSolution);
    }

    public void RequestHint(){
        if(hintsLeft > 0)
            GetOnlineSolvedBoard(NetworkRequestType.requestHint);
    }


}
