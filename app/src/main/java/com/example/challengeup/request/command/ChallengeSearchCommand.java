package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.dto.ChallengeSearchDTO;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class ChallengeSearchCommand implements IRequestCommand {

    private final ChallengeSearchDTO challengeSearch;

    public ChallengeSearchCommand(ChallengeSearchDTO challengeSearch) {
        this.challengeSearch = challengeSearch;
    }

    @Override
    public Result request() {
        List<ChallengeEntity> challenges = ChallengeEntity.search(
                challengeSearch.getQuery(),
                challengeSearch.getLiked(),
                challengeSearch.getAccepted(),
                challengeSearch.getCompleted(),
                challengeSearch.getRp(),
                challengeSearch.getCategories(),
                challengeSearch.getOrderBy(),
                challengeSearch.getDirection()
        );
        return new Result.Success<>(challenges);
    }
}
