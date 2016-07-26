package com.mpsdevelopment.biopotential.server.db.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;


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
    
    

}
