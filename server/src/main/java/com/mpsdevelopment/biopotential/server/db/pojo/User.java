package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.DateTypeAdapter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "User")
public class User extends BaseObject {

    public static final String LOGIN_FIELD = "login";
    public static final String LOGIN_FIELD_GS = "l";
    public static final String PASSWORD_FIELD = "password";
    public static final String PASSWORD_FIELD_GS = "p";
    public static final String NAME_FIELD = "name";
    public static final String NAME_FIELD_GS = "n";
    public static final String SURNAME_FIELD = "surname";
    public static final String SURNAME_FIELD_GS = "s";
    public static final String PATRONYMIC_FIELD = "patronymic";
    public static final String PATRONYMIC_FIELD_GS = "patr";
    public static final String ROLE_FIELD = "role";
    public static final String ROLE_FIELD_GS = "r";
    public static final String RANK_FIELD = "rank";
    public static final String RANK_FIELD_GS = "rank";
    public static final String DIVISION_FIELD = "division";
    public static final String DIVISION_FIELD_GS = "div";
    public static final String CALL_FIELD = "call";
    public static final String CALL_FIELD_GS = "call";
    public static final String ADMIN_FIELD = "admin";
    public static final String ADMIN_FIELD_GS = "adm";
    public static final String TEL_FIELD = "tel";
    public static final String TEL_FIELD_GS = "t";
    public static final String EMAIL_FIELD = "email";
    public static final String EMAIL_FIELD_GS = "e";
    public static final String BORNPLACE_FIELD = "bornPlace";
    public static final String BORNPLACE_FIELD_GS = "bp";
    public static final String BORNDATE_FIELD = "bornDate";
    public static final String BORNDATE_FIELD_GS = "bd";
    public enum Gender{
        Мужчина,
        Женщина
    };

    @OneToMany(mappedBy = "user")
    private List<Visit> visits = new ArrayList<>();

    @Expose
    @Column(name = LOGIN_FIELD)
    @SerializedName(LOGIN_FIELD_GS)
    private String login;

    //@Expose(serialize = false, deserialize = true)
    @Expose
    @Column(name = PASSWORD_FIELD)
    @SerializedName(PASSWORD_FIELD_GS)
    private String password;

    @Expose
    @Column(name = NAME_FIELD)
    @SerializedName(NAME_FIELD_GS)
    private String name;

    @Expose
    @Column(name = SURNAME_FIELD)
    @SerializedName(SURNAME_FIELD_GS)
    private String surname;
    
    @Expose
    @Column(name = PATRONYMIC_FIELD)
    @SerializedName(PATRONYMIC_FIELD_GS)
    private String patronymic;
    
    @Expose
    @Column(name = ROLE_FIELD)
    @SerializedName(ROLE_FIELD_GS)
    private String role;
    
    @Expose
    @Column(name = RANK_FIELD)
    @SerializedName(RANK_FIELD_GS)
    private String rank;
    
    @Expose
    @Column(name = DIVISION_FIELD)
    @SerializedName(DIVISION_FIELD_GS)
    private String division;
    
    @Expose
    @Column(name = CALL_FIELD)
    @SerializedName(CALL_FIELD_GS)
    private String call;
    
    @Expose
    @Column(name = ADMIN_FIELD)
    @SerializedName(ADMIN_FIELD_GS)
    private Boolean admin;

    @Expose
    @Column(name = TEL_FIELD)
    @SerializedName(TEL_FIELD_GS)
    private String tel;

    @Expose
    @Column(name = EMAIL_FIELD)
    @SerializedName(EMAIL_FIELD_GS)
    private String email;

    @Expose
    @Column(name = BORNPLACE_FIELD)
    @SerializedName(BORNPLACE_FIELD_GS)
    private String bornPlace;

    @Expose
    @Column(name = BORNDATE_FIELD)
    @SerializedName(BORNDATE_FIELD_GS)
    private Date bornDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public User setSurname(String surname) {
        this.surname = surname;
        return this;
    }

	public String getPatronymic() {
		return patronymic;
	}

	public User setPatronymic(String patronymic) {
		this.patronymic = patronymic;
		return this;
	}

	public String getRank() {
		return rank;
	}

	public User setRank(String rank) {
		this.rank = rank;
		return this;
	}

	public String getDivision() {
		return division;
	}

	public User setDivision(String division) {
		this.division = division;
		return this;
	}

	public String getCall() {
		return call;
	}

	public User setCall(String call) {
		this.call = call;
		return this;
	}

	public Boolean getAdministrator() {
		return admin;
	}

	public User setAdministrator(Boolean admin) {
		this.admin = admin;
		return this;
	}

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBornPlace() {
        return bornPlace;
    }

    public void setBornPlace(String bornPlace) {
        this.bornPlace = bornPlace;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }
}
