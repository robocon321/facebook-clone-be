package com.example.demo.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.CheckinRequest;
import com.example.demo.dto.response.CheckinResponse;
import com.example.demo.entity.CheckinEntity;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.response.PageResponse;

@Service
public class CheckinService {
	@Autowired
	private CheckinRepository checkinRepository;
	
	public PageResponse search(CheckinRequest request) {
		Pageable pageable = null;
		if(request.getSortBy() == null) {
			pageable = PageRequest.of(request.getPage(), request.getSize());
		} else {
			Sort.Order[] orders = IntStream.range(0, request.getSortBy().length).mapToObj(
					i -> new Sort.Order(Sort.Direction.fromString(request.getSortOrder()[i]), request.getSortBy()[i]))
					.toArray(Sort.Order[]::new);

			Sort sort = Sort.by(orders);
			pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		}
		Page<CheckinEntity> pageEntity = checkinRepository.searchCheckin(request.getSearch(), pageable);
		List<CheckinResponse> pageDTO = pageEntity.stream()
				.map(checkin -> {
					CheckinResponse response = new CheckinResponse();
					BeanUtils.copyProperties(checkin, response);
					return response;
				}).toList();
		
		PageResponse pageResponse = PageResponse.builder()
				.data(pageDTO)
				.totalItem(pageEntity.getTotalElements())
				.totalPage(pageEntity.getTotalPages()).build();

		return pageResponse;
	}
}
