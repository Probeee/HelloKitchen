package tw.org.iii.hellokitchen.Entity;


import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by Kevin on 2017/6/3.
 */

public class Ingredients
{
    private String _id;
    private String name;
    private String buyDate;
    private String deadDate;
    private String member_id;
    private Integer amount;
    private String day;



    public Long time;//計算時間用


    public Long getTime()
    {
        return time;
    }



    public Ingredients()
    {
        this.name ="";
        this.buyDate ="";
        this.deadDate ="";
        this.amount = 0;
    }

    public Ingredients(String _id,String name,String buyDate,String deadDate,String amount,String member_id)
    {
        this._id = _id;
        this.name = name;
        this.buyDate = buyDate;
        this.deadDate = deadDate;
        this.member_id = member_id;
        this.amount = Integer.parseInt(amount);
        this.day = getDayLeft(deadDate);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getDeadDate() {
        return deadDate;
    }

    public void setDeadDate(String deadDate) {
        this.deadDate = deadDate;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id)
    {
        this.member_id = member_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void AddAmount()
    {
        this.amount ++;
    }

    public void MinusAmount()
    {
        if (this.amount <= 0)
        {
            this.amount = 0;
        }
        else
        {
            this.amount --;
        }
    }

    private String getDayLeft(String deadDate)
    {
        String betweenDate = "0" ;
        Long buff = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try
        {
            Date DeadDate = null;
            DeadDate = sdf.parse(deadDate);
            Date dt = new Date();
            buff = DeadDate.getTime()-dt.getTime() ;
            if(buff<0)
            {
                betweenDate = "已過期";
                time = buff;
            }
            else
            {
                buff /= 1000L;
                buff /= 60L;
                buff /= 60L;
                buff /= 24L;
                time = buff;
                betweenDate = String.valueOf(buff);
            }


        } catch (ParseException e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            return  betweenDate;
        }

    }

    public String getDay()
    {
        return this.day;
    }

}
