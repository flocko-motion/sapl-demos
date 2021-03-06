package org.demo.pip;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.demo.domain.Relation;
import org.demo.domain.RelationRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import io.sapl.grammar.sapl.impl.Val;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final ObjectMapper mapper;

	private final RelationRepository relationRepo;

	private final PatientRepository patientRepo;

	@Attribute(name = "relatives")
	public Flux<Val> getRelations(@Number Val value, Map<String, JsonNode> variables) {
		final List<Relation> relations = relationRepo.findByPatientid(value.get().asLong());
		final List<String> relationNames = relations.stream().map(Relation::getUsername).collect(Collectors.toList());
		final JsonNode jsonNode = mapper.convertValue(relationNames, JsonNode.class);
		return Flux.just(Val.of(jsonNode));
	}

	@Attribute(name = "patientRecord")
	public Flux<Val> getPatientRecord(@Number Val patientId, Map<String, JsonNode> variables) {
		try {
			final Patient patient = patientRepo.findById(patientId.get().asLong()).orElseThrow(AttributeException::new);
			final JsonNode jsonNode = mapper.convertValue(patient, JsonNode.class);
			return Flux.just(Val.of(jsonNode));
		} catch (IllegalArgumentException | AttributeException e) {
			return Flux.just(Val.ofNull());
		}
	}

}
