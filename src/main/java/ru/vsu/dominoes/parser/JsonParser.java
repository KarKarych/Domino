package ru.vsu.dominoes.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.vsu.dominoes.model.Chip;
import ru.vsu.dominoes.model.enums.Moves;
import ru.vsu.dominoes.utils.Pair;

import java.io.IOException;
import java.util.LinkedList;

public class JsonParser {
  public String parseMarketJson(LinkedList<Chip> chipsList) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode main = mapper.createObjectNode();

    ArrayNode chips = mapper.createArrayNode();

    for (Chip chip : chipsList) {
      ObjectNode chipNode = mapper.createObjectNode();
      chipNode.put("number_1", chip.getNumber1());
      chipNode.put("number_2", chip.getNumber2());

      chips.add(chipNode);
    }

    main.set("chips", chips);

    String jsonString = null;

    try {
      jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonString;
  }

  public LinkedList<Chip> parseMarketList(String jsonString) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode main = null;

    try {
      main = mapper.readTree(jsonString);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    assert main != null;

    LinkedList<Chip> chipsList = new LinkedList<>();
    ArrayNode chips = (ArrayNode) main.get("chips");

    for (JsonNode chipNode : chips) {
      chipsList.add(new Chip(chipNode.get("number_1").asInt(), chipNode.get("number_2").asInt()));
    }

    return chipsList;
  }

  public String parseMoveAndChipJson(Moves move, Chip chip) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode main = mapper.createObjectNode();

    ObjectNode chipNode = mapper.createObjectNode();
    chipNode.put("number_1", chip.getNumber1());
    chipNode.put("number_2", chip.getNumber2());

    main.set("chip", chipNode);
    main.put("move", move.ordinal());

    String jsonString = null;

    try {
      jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonString;
  }

  public Pair<Chip, Moves> parseMoveAndChipObjects(String jsonString) {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode main = null;

    try {
      main = mapper.readTree(jsonString);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    assert main != null;

    JsonNode chipNode = main.get("chip");

    if (chipNode != null) {
      Chip chip = new Chip(chipNode.get("number_1").asInt(), chipNode.get("number_2").asInt());
      Moves move = Moves.values()[main.get("move").asInt()];
      return new Pair<>(chip, move);
    }

    return null;
  }
}
