package com.shuai.musicplayer2.domain;

import java.util.List;

public class CommentList {

    private java.util.List<HotCommentsBean> hotComments;

    public List<HotCommentsBean> getHotComments() {
        return hotComments;
    }

    public void setHotComments(List<HotCommentsBean> hotComments) {
        this.hotComments = hotComments;
    }

    public static class HotCommentsBean {
        /**
         * user : {"locationInfo":null,"liveInfo":null,"anonym":0,"userId":52210431,"avatarDetail":null,"userType":0,"remarkName":null,"vipRights":null,"nickname":"花村村花的男人","avatarUrl":"https://p3.music.126.net/Uo2ExBQZp2uU1K6KaWKUgg==/7797736464366291.jpg","authStatus":0,"expertTags":null,"experts":null,"vipType":0}
         * content : 杰伦以前的冷门歌曲现在看来简直就是巨大宝藏！
         */

        private UserBean user;
        private String content;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

        public static class UserBean {
            /**
             * userId : 52210431
             * nickname : 花村村花的男人
             * avatarUrl : https://p3.music.126.net/Uo2ExBQZp2uU1K6KaWKUgg==/7797736464366291.jpg
             */
            private int userId;
            private String nickname;
            private String avatarUrl;

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }
        }
}
