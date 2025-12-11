package esy.rest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JsonJpaRepository<V extends JsonJpaEntity<V>> extends JpaRepository<V, UUID> {
}
