package com.coffebara.summaryBot.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Member {

    private int idCode;
    private String name;
    private int ranking;
    private String ally;
    private int power;
    private int death;
    private int totalKillPoint;
    private int killPoint4T;
    private int killPoint5T;
    private String mainImg;
    private String detailImg1;
    private String detailImg2;

    public Member(String name, int ranking, String mainImg, String detailImg1, String detailImg2) {
        this.name = name;
        this.ranking = ranking;
        this.mainImg = mainImg;
        this.detailImg1 = detailImg1;
        this.detailImg2 = detailImg2;
    }

    public void setMemberMainData(int idCode, String ally, int power, int death, int totalKillPoint, int killPoint4T, int killPoint5T) {
        this.idCode = idCode;
        this.ally = ally;
        this.power = power;
        this.death = death;
        this.totalKillPoint = totalKillPoint;
        this.killPoint4T = killPoint4T;
        this.killPoint5T = killPoint5T;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return getIdCode() == member.getIdCode() && getRanking() == member.getRanking() && Objects.equals(getName(), member.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdCode(), getName(), getRanking());
    }

    @Override
    public String toString() {
        return "Member{" +
                "idCode=" + idCode +
                ", name='" + name + '\'' +
                ", ranking=" + ranking +
                ", ally='" + ally + '\'' +
                ", power=" + power +
                ", death=" + death +
                ", totalKillPoint=" + totalKillPoint +
                ", killPoint4T=" + killPoint4T +
                ", killPoint5T=" + killPoint5T +
                '}';
    }
}
