package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(Long id, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%')) AND " +
            "i.available = :available")
    List<Item> search(@Param("name") String name, @Param("description") String description,
                      @Param("available") Boolean available, Pageable pageable);

    List<Item> getItemsByRequestId(Long requestId, Sort sort);
}