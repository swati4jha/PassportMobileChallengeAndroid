package com.swati.passport.mobilechallenge;

import java.util.Comparator;

public class UserVo {
    private long _id;
    private String userName;
    private long age;
    private String gender;
    private String userImage;
    private String hobbies;

    public UserVo()
    {

    }

    public UserVo(long _id, String userName, long age, String gender, String userImage, String hobbies) {
        this._id = _id;
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.userImage = userImage;
        this.hobbies = hobbies;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "_id='" + _id + '\'' +
                ", userName='" + userName + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", userImage='" + userImage + '\'' +
                ", hobbies='" + hobbies + '\'' +
                '}';
    }

    /* Comparator for sorting the list by User Name Ascending */
    public static Comparator<UserVo> UserNameComparatorAsc = new Comparator<UserVo>() {

        public int compare(UserVo u1, UserVo u2) {
            String name1 = u1.getUserName().toUpperCase();
            String name2 = u2.getUserName().toUpperCase();
            return name1.compareTo(name2);

        }};

    /* Comparator for sorting the list by User Name Descending */
    public static Comparator<UserVo> UserNameComparatorDesc = new Comparator<UserVo>() {

        public int compare(UserVo u1, UserVo u2) {
            String name1 = u1.getUserName().toUpperCase();
            String name2 = u2.getUserName().toUpperCase();
            return name2.compareTo(name1);

        }};

    /* Comparator for sorting the list by User Age Ascending */
    public static Comparator<UserVo> UserAgeComparatorAsc = new Comparator<UserVo>() {

        public int compare(UserVo u1, UserVo u2) {
            Long age1 = u1.getAge();
            Long age2 = u2.getAge();
            return age1.compareTo(age2);

        }};

    /* Comparator for sorting the list by User Name Descending */
    public static Comparator<UserVo> UserAgeComparatorDesc = new Comparator<UserVo>() {

        public int compare(UserVo u1, UserVo u2) {
            Long age1 = u1.getAge();
            Long age2 = u2.getAge();
            return age2.compareTo(age1);

        }};

    /* Comparator for sorting the list by id */
    public static Comparator<UserVo> UserIDComparator = new Comparator<UserVo>() {

        public int compare(UserVo u1, UserVo u2) {
            Long id1 = u1.get_id();
            Long id2 = u2.get_id();
            return id1.compareTo(id2);

        }};

}
