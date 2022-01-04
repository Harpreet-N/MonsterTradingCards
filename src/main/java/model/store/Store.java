package model.store;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Store {
    private final List<Trade> store = new ArrayList<>();
}
