package com.digisprint.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digisprint.bean.RegistrationFrom;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.service.RegistrationService;

@Service
public class RegistrationServiceImpl  implements RegistrationService{

	@Autowired
	private RegistrationFromRepository registrationFromRepository;

	@Override
	public RegistrationFrom registerUser(RegistrationFrom from) {
	
		 registrationFromRepository.save(from);
		 System.out.println(registrationFromRepository.findAll().size());
		 return registrationFromRepository.findAll().get(0);
	}
	
	
}
