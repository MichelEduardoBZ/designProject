package br.com.testDesign.util;

import br.com.testDesign.entities.BasicEntity;
import br.com.testDesign.projection.IdProjection;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageUtils {

    public static <T extends IdProjection<Long>, E extends BasicEntity> List<E> replaceSortByContent(List<T> ordered, List<E> unordered) {
        Map<Long, E> map = unordered.stream()
                .collect(Collectors.toMap(
                        BasicEntity::getId,
                        entity -> entity
                ));

        return ordered.stream()
                .map(item -> map.get(item.getId()))
                .filter(Objects::nonNull)
                .toList();
    }
}


