package org.demo.pip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.demo.domain.PatientRepository;
import org.demo.domain.Relation;
import org.demo.domain.RelationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final ObjectMapper mapper;
	private final RelationRepository relationRepo;
	private final PatientRepository patientRepo;

	@Attribute(name = "relatives")
	public JsonNode getRelations(@Number JsonNode value, Map<String, JsonNode> variables) {
		List<String> returnList = new ArrayList<>();
		returnList.addAll(relationRepo.findByPatientid(value.asLong()).stream().map(Relation::getUsername)
				.collect(Collectors.toList()));
		return mapper.convertValue(returnList, JsonNode.class);
	}

	//@PreAuthorize("ROLE_VISITOR")
	@Attribute(name = "patientRecord")
	public JsonNode getPatientRecord(@Number JsonNode patientId, Map<String, JsonNode> variables) {
		try {
			return mapper.convertValue(patientRepo.findById(patientId.asLong()).orElseThrow(AttributeException::new),
					JsonNode.class);
		} catch (IllegalArgumentException | AttributeException e) {
			return JsonNodeFactory.instance.nullNode();
		}
	}
}