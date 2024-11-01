package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User as u
            """)
    List<User> findAllUsersWithPagination(Pageable pageable);

    @Query("""
            select u
            from User as u
            where u.id in :ids
            order by u.id asc
            """)
    List<User> findByIds(List<Integer> ids);
}