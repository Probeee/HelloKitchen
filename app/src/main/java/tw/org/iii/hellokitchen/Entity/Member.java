package tw.org.iii.hellokitchen.Entity;

/**
 * Created by Kevin on 2017/6/7.
 */

public class Member
{
    private String member_id;
    private String member_name;
    private String member_email;
    private String member_password;
    private String member_tel;
    private String member_fb;

    public Member()
    {
    }

    public Member(String member_id, String member_name, String member_email, String member_password, String member_tel, String member_fb)
    {
        this.member_id = member_id;
        this.member_name = member_name;
        this.member_email = member_email;
        this.member_password = member_password;
        this.member_tel = member_tel;
        this.member_fb = member_fb;
    }

    public String getMember_id()
    {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getMember_password() {
        return member_password;
    }

    public void setMember_password(String member_password) {
        this.member_password = member_password;
    }

    public String getMember_tel() {
        return member_tel;
    }

    public void setMember_tel(String member_tel) {
        this.member_tel = member_tel;
    }

    public String getMember_fb() {
        return member_fb;
    }

    public void setMember_fb(String member_fb) {
        this.member_fb = member_fb;
    }
}
