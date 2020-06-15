package com.example.challengeup.request.command;

import com.example.challengeup.backend.Utilities;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class GetCategories implements IRequestCommand {
    @Override
    public Result request() {
        ArrayList<String> c = Utilities.getCategories();
        return new Result.Success<>(c);
    }
}
