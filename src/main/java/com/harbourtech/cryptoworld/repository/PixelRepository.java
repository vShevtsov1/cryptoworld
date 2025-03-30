package com.harbourtech.cryptoworld.repository;

import com.harbourtech.cryptoworld.entity.Pixel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PixelRepository extends JpaRepository<Pixel, Long> {

    List<Pixel> findByXInAndYIn(List<Integer> x, List<Integer> y);



}
