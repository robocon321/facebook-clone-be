package com.example.demo.type;

public enum ErrorCodeType {
    ERROR_ACCOUNT_NOT_FOUND("Your account does not exists"),
    ERROR_ACCOUNT_FOUND("Your account has already existed"),
    ERROR_ACCOUNT_SPECIFIC_NOT_FOUND("Account {0} does not exists"),
    ERROR_ACCOUNT_BLOCKED("Your account is blocked"),
    ERROR_ACCOUNT_SPECIFIC_BLOCKED("Account {0} is blocked"),
    ERROR_ACCOUNT_SPECIFIC_BLOCKED_YOU("Account {0} has blocked you"),
    ERROR_ACCOUNT_MATCHED("Receiver is similar sender"),
    ERROR_NOT_FOUND_RELATIONSHIP("Not found relationship"),
    ERROR_PERMISSION_DENY("Do not have permission"),
    ERROR_ACCOUNT_EXIST("Account has already existed"),
    ERROR_ACCOUNT_SPECIFIC_MATCH_FRIENDSHIP("Account {0} has sent request friendship before"),
    ERROR_ACCOUNT_MATCH_FRIENDSHIP("Your account has sent request friendship before"),
    ERROR_ACCOUNT_ACCEPT_FRIENDSHIP("Your account has accept request friendship before"),
    ERROR_FILE_NOT_FOUND("File not found"),
    ERROR_FILE_INVALID("File is invalid"),
    ERROR_REQUIRE_LOGIN("Please login"),
    ERROR_CHECKIN_SPECIFIC_NOT_FOUND("Checkin {0} is not found"),
    ERROR_TAG_SPECIFIC_NOT_FOUND("Tag {0} not found"),
    ERROR_ACCOUNT_SPECIFIC_NOT_FRIEND("AccountID: {0} is not your friend"),
    ERROR_CANNOT_SAVE_FILE("Sorry, we cannot save file {0}"),
    ERROR_ARTICLE_SPECIFIC_NOT_FOUND("Article {0} is not found"),
    ERROR_COMMENT_SPECIFIC_NOT_FOUND("Comment {} is not found");

    private String message;

    ErrorCodeType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
