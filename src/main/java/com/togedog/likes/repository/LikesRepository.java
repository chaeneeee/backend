package com.togedog.likes.repository;

import com.togedog.board.entity.Board;
import com.togedog.likes.entity.Likes;
import com.togedog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes,Long> {
    Optional<Likes> findByMember(Member member);
    Optional<Likes> findByMemberAndBoard(Member member, Board board);
}
