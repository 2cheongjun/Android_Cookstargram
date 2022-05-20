package com.example.myapplication.Api;

public class Api {

    public static final String BASE_URL = "http://3.37.202.166";

    // 포스트
    public static final String URL_COOK_POST_INSERT = BASE_URL + "/post/cookPostInsert.php";
    // 올린 글 조회
    public static final String URL_COOK_POST_SELECT = BASE_URL + "/post/cookPostSelect.php";


    // 글 업데이트
    public static final String URL_COOK_POST_UPDATE = BASE_URL + "/post/cookPostUpdate.php";
    // 글 삭제
    public static final String URL_COOK_POST_DELETE = BASE_URL + "/post/cookPostDelete.php";
    // 좋아요
    public static final String URL_COOK_POST_LIKE = BASE_URL + "/post/cookPostLike.php";

    // 쿡스타 검색
    public static final String URL_COOK_POST_SEARCH = BASE_URL + "/post/cookPostSelectSearch.php";

    // 북마크 DB에 넣기
    public static final String URL_BOOKMARK = BASE_URL + "/bookmark/bookmark.php";
    // 북마크 썸네일 조회
    public static final String URL_BOOKMARK_SELECT = BASE_URL + "/bookmark/bookmarkSelect.php";
    // 북마크 상세
    public static final String URL_BOOKMARK_SELECT_VIEW = BASE_URL + "/bookmark/bookViewSelect.php";

    // TODO dbconfing.php 위치 확인
    // 마이페이지
    // 프로필 사진 조회
    public static final String URL_PROFILE_SELECT = BASE_URL + "/mypage/profileSelect.php";

    // 대화상대방 프로필 사진 조회
    public static final String URL_FRIEND_PROFILE_SELECT = BASE_URL + "/mypage/profileFriendSelect.php";
    // 프로필사진 업로드
    public static final String URL_PROFILE_INSERT = BASE_URL + "/mypage/profileInsert.php";

    // 댓글 작성 insert
    public static final String URL_REPLY = BASE_URL + "/reply/reply.php";
    // 댓글 조회 select
    public static final String URL_REPLY_SELECT = BASE_URL + "/reply/replySelect.php";
    // 댓글 삭제
    public static final String URL_REPLY_DELETE = BASE_URL + "/reply/replyDelete.php";
    // 댓글 수정
    public static final String URL_REPLY_UPDATE = BASE_URL + "/reply/replyUpdate.php";

    // 답글 작성
    public static final String URL_SECOND_REPLY = BASE_URL + "/reply/secondReply.php";

    // 답글 작성
    public static final String URL_REPLY_PROFILE_SELECT = BASE_URL + "/reply/profile.php";

    // 북마크 썸네일 조회
    public static final String URL_STORY_SELECT = BASE_URL + "/mypage/friendStorySelect.php";

    // 채팅방 사진업로드
    public static final String URL_CHAT_IMG_INSERT = BASE_URL + "/post/chatImgInsert.php";
}

