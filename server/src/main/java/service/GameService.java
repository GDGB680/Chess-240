package service;

import chess.*;
import dataaccess.*;
import model.*;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
