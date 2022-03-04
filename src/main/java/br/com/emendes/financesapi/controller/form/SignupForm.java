package br.com.emendes.financesapi.controller.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import br.com.emendes.financesapi.model.User;
public class SignupForm {

  @NotBlank
  private String name;
  @NotBlank
  @Email
  private String email;
  @NotBlank
  // TODO: Pensar numa annotation para validar uma senha forte
  private String password;
  @NotBlank
  private String confirm;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirm() {
    return confirm;
  }

  public void setConfirm(String confirmed) {
    this.confirm = confirmed;
  }

  public User toUser(){
    return new User(name, email, password);
  }

  public boolean isMatch(){
    return this.password.equals(this.confirm);
  }

}
