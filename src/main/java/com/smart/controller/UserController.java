package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.healper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);

		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String index(Model model, Principal principal) {

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_from";
	}

	@PostMapping("/process-contact")
	public String process_Contact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();

			System.out.println("file Name " + file.getOriginalFilename());
			System.out.println("type : " + file.getContentType());

			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("default_contacts_logo.png");
			} else {

				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			User user = userRepository.getUserByUserName(name);
			user.getContacts().add(contact);
			contact.setUser(user);
			userRepository.save(user);
			System.out.println("contact " + contact);
			session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Some went wrong !! Ttry again", "danger"));
		}

		return "normal/add_contact_from";
	}

	// Show contact handler
	@GetMapping("/show-contacts/{numberOfPage}")
	public String showContacts(@PathVariable("numberOfPage") int numberOfPage, Model model, Principal principal) {
		model.addAttribute("title", "Show User Contacts");

		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);

		Pageable pageable = PageRequest.of(numberOfPage, 2);

		Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", numberOfPage);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show-contacts";
	}

	// showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetalis(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		try {
			Optional<Contact> contactOptional = contactRepository.findById(cId);

			Contact contact = null;
			if (contactOptional.isPresent()) {
				contact = contactOptional.get();
			}

			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);

			if (user.getId() == contact.getUser().getId()) {

				model.addAttribute("title", contact.getName());
				model.addAttribute("contact", contact);

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("inside exception");
		}

		return "normal/contact_details";
	}

	@GetMapping("delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, HttpSession session) {

		System.out.println("inside delete handeler " + cId);

		Contact contact = contactRepository.findById(cId).get();
		contact.setUser(null);
		contactRepository.delete(contact);

		session.setAttribute("message", new Message("Contact deleted successfully", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model m) {
		m.addAttribute("title", "Update Contacts");

		System.out.println("cid : " + cId);

		Contact contact = contactRepository.findById(cId).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact process handler
	@RequestMapping(value = "/process-contact-update", method = RequestMethod.POST)
	public String updateContactProcess(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Model m, Principal principal, HttpSession session) {

		try {

			Contact oldContact = contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				// delete old photo

				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContact.getImage());
				file1.delete();

				// update new photot
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContact.getImage());
			}

			String userName = principal.getName();
			User user = userRepository.getUserByUserName(userName);
			contact.setUser(user);

			contactRepository.save(contact);

			session.setAttribute("message", new Message("Your contact is updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

}
