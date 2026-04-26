package com.feelsent.dto;

import com.feelsent.enums.RelationshipType;
import lombok.Data;

@Data
public class AcceptRequest {
    private RelationshipType relationshipType;
}
