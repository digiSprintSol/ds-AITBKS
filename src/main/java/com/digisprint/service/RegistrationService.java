package com.digisprint.service;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.digisprint.bean.RegistrationFrom;

public interface RegistrationService {

	RegistrationFrom registerUser(RegistrationFrom from);

}
