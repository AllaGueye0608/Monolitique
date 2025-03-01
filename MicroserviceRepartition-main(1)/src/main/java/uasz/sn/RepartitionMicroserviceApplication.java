package uasz.sn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uasz.sn.model.Enseignant;
import uasz.sn.model.Enseignement;
import uasz.sn.service.EnseignantService;
import uasz.sn.service.EnseignementService;

import java.util.List;

@EnableFeignClients
//@EnableDiscoveryClient
@SpringBootApplication
public class RepartitionMicroserviceApplication implements CommandLineRunner {
	@Autowired
	private EnseignementService enseignementService;
	@Autowired
	private EnseignantService enseignantService;
	public static void main(String[] args) {
		SpringApplication.run(RepartitionMicroserviceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*List<Enseignement> enseignements = enseignementService.getAll();
		List<Enseignant> enseignants = enseignantService.getAll();
		enseignements.forEach(enseignement->{
			enseignementService.save(enseignement);
		});
		enseignants.forEach(enseignant -> {
			enseignantService.save(enseignant);
		});*/
		for(int i=0;i<5;i++){
			enseignantService.save(new Enseignant());
			enseignementService.save(new Enseignement());
		}

	}
}