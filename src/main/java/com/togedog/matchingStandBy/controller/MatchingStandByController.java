package com.togedog.matchingStandBy.controller;

import com.togedog.dto.SingleResponseDto;
import com.togedog.matchingStandBy.dto.MatchingStandByDto;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.matchingStandBy.mapper.MatchingStandByMapper;
import com.togedog.matchingStandBy.service.MatchingStandByService;
import com.togedog.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/matchings/stand-by")
@Validated
@CrossOrigin(origins = "http://localhost:3000")  // localhost:3000에서 오는 요청을 허용
@RequiredArgsConstructor
public class MatchingStandByController {
    private final static String MATCHING_STAND_BY_DEFAULT_URI = "/matchings/stand-by";
    private final MatchingStandByMapper mapper;
    private final MatchingStandByService service;

    @PostMapping("/{matching-id}")
    public ResponseEntity postMatchingStandBy(@PathVariable("matching-id") @Positive long matchingId,
                                              Authentication authentication) {
        MatchingStandBy createMatchingStandBy = service. createMatchingStandBy(authentication,matchingId);
        URI location = UriCreator.createUri(MATCHING_STAND_BY_DEFAULT_URI, createMatchingStandBy.getMatchingStandById());
        return ResponseEntity.created(location).build();
    }
    @PatchMapping("/{matching-stand-by-id}")
    public ResponseEntity patchMatchingStandBy(@PathVariable("matching-stand-by-id") @Positive long matchingStandById,
                                               @Valid @RequestBody MatchingStandByDto.Patch requestBody,
                                               Authentication authentication) {
        requestBody.setMatchingStandById(matchingStandById);
        MatchingStandBy updateMatching =
                service.updateMatchingStandBy(mapper.matchingStandByPatchDtoToMatchingStandBy(requestBody),authentication);
        return new ResponseEntity<>(HttpStatus.OK);
//        return new ResponseEntity<>(
//                new SingleResponseDto<>(mapper.matchingToMatchingResponseDto(updateMatching)),
//                HttpStatus.OK);
    }
    @GetMapping("/{matching-id}")
    public ResponseEntity getHostMatchingStandBys(@PathVariable("matching-id") @Positive long matchingId,
                                                  Authentication authentication){
        boolean result = service.findMatchAlreadyExists(matchingId,authentication);
        if(result)
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/host")
    public ResponseEntity getHostMatchingStandBys(Authentication authentication){
        List<MatchingStandBy> MatchingStandBys =
                service.findHostMatchingStandBys(authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.matchingStandBysToMatchingStandByDtoResponseHost(MatchingStandBys)),
                HttpStatus.OK);
    }
    @GetMapping("/guest")
    public ResponseEntity getGuestMatchingStandBys(Authentication authentication){
        List<MatchingStandBy> MatchingStandBys =
                service.findGuestMatchingStandBys(authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.matchingStandBysToMatchingStandByDtoResponseGuest(MatchingStandBys)),
                HttpStatus.OK);
    }
//    @GetMapping("/host")
//    public ResponseEntity getHostMatchingStandBys(@Positive @RequestParam int page,
//                                                  @Positive @RequestParam int size,
//                                                  Authentication authentication){
//        Page<MatchingStandBy> pageMatchingStandBys =
//                service.findHostMatchingStandBys(page - 1, size,authentication);
//        List<MatchingStandBy> MatchingStandBys = pageMatchingStandBys.getContent();
//
//        return new ResponseEntity<>(
//                new MultiResponseDto<>(mapper.matchingStandBysToMatchingStandByDtoResponseHost(MatchingStandBys),
//                        pageMatchingStandBys),HttpStatus.OK);
//    }

//    @GetMapping("/guest")
//    public ResponseEntity getGuestMatchingStandBys(@Positive @RequestParam int page,
//                                                   @Positive @RequestParam int size,
//                                                   Authentication authentication){
//        Page<MatchingStandBy> pageMatchingStandBys =
//                service.findGuestMatchingStandBys(page - 1, size,authentication);
//        List<MatchingStandBy> MatchingStandBys = pageMatchingStandBys.getContent();
//
//        return new ResponseEntity<>(
//                new MultiResponseDto<>(mapper.matchingStandBysToMatchingStandByDtoResponseGuest(MatchingStandBys),
//                        pageMatchingStandBys), HttpStatus.OK);
//    }
}
