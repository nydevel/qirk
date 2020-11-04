package org.wrkr.clb.services.user;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;

@Validated
public interface RegistrationService {

    public ExistsDTO checkEmail(
            @NotEmpty(message = "email must not be empty") @Email(message = "email must be valid") String email);

    public ExistsDTO checkUsername(
            @NotNull(message = "username must not be null") @Pattern(regexp = RegExpPattern.LOWER_CASE_SLUG
                    + "+", message = "username must be lower case slug") String username);

    public User createUserWithEmailAndPasswordHash(
            @NotEmpty(message = "email must not be empty") @Email(message = "email must be valid") String email,
            @NotBlank(message = "passwordHash must not be blank") String passwordHash,
            @NotNull(message = "username must not be null") @Pattern(regexp = RegExpPattern.LOWER_CASE_SLUG
                    + "{1,25}", message = "username must be lower case slug") String username,
            @NotBlank(message = "fullName must not be blank") String fullName) throws Exception;

    public EmailSentDTO register(@Valid EmailAddressDTO registerDTO) throws Exception;

    public User activate(@Valid ActivationDTO activationDTO) throws Exception;
}
