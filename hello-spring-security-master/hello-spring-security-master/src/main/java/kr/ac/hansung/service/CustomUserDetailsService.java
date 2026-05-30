package kr.ac.hansung.service;

import kr.ac.hansung.entity.User;
import kr.ac.hansung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 이메일로 사용자 조회 — 없으면 UsernameNotFoundException 발생
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 사용자의 Role 목록을 Spring Security가 요구하는 GrantedAuthority 목록으로 변환
        // ex) Role("ROLE_USER"), Role("ROLE_ADMIN") → [SimpleGrantedAuthority("ROLE_USER"), SimpleGrantedAuthority("ROLE_ADMIN")]
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName()))
            .toList();

        // Spring Security 내부에서 사용할 UserDetails 객체 생성 (이메일을 username으로 사용)
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), authorities
        );
    }
}
