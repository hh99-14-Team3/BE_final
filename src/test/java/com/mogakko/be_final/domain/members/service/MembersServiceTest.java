//package com.mogakko.be_final.domain.members.service;
//
//import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
//import com.mogakko.be_final.domain.members.entity.Members;
//import com.mogakko.be_final.domain.members.repository.MembersRepository;
//import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
//import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomTimeRepository;
//import com.mogakko.be_final.exception.CustomException;
//import com.mogakko.be_final.util.Message;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_IDENTIFIER;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MembersServiceTest {
//
//    @Mock
//    private MembersRepository membersRepository;
//
//    @Mock
//    private MogakkoRoomTimeRepository mogakkoRoomTimeRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private MembersService membersService;
//
////    @BeforeEach
////    public void setUp() {
////        MockitoAnnotations.openMocks(this);
////    }
//
//    @DisplayName("회원 가입 성공 테스트")
//    @Test
//    void signup_Success() {
//        // given
//        SignupRequestDto requestDto = new SignupRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("password");
//        requestDto.setNickname("nickname");
//
//        String encodedPassword = passwordEncoder.encode("password");
//        String email = requestDto.getEmail();
//
//        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(encodedPassword);
//        when(membersRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        // when
//        ResponseEntity<Message> response = membersService.signup(requestDto);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("회원 가입 성공", response.getBody().getMessage());
//
//        verify(passwordEncoder).encode(requestDto.getPassword());
//        verify(membersRepository).findByEmail(email);
//        verify(membersRepository).save(any(Members.class));
//        verify(mogakkoRoomTimeRepository).save(any(MogakkoRoomTime.class));
//    }
//
//    @DisplayName("이미 가입된 이메일로 회원 가입 시도")
//    @Test
//    void signup_AlreadyJoinedEmail() {
//        // given
//        SignupRequestDto requestDto = new SignupRequestDto();
//        requestDto.setEmail("test@example.com");
//        requestDto.setPassword("password");
//        requestDto.setNickname("nickname");
//
//        String email = requestDto.getEmail();
//
//        when(membersRepository.findByEmail(email)).thenReturn(Optional.of(new Members()));
//
//        // when / then
//        CustomException exception = assertThrows(CustomException.class, () -> membersService.signup(requestDto));
//        assertEquals(DUPLICATE_IDENTIFIER, exception.getErrorCode());
//
//        verify(membersRepository).findByEmail(email);
//        verify(membersRepository, never()).save(any(Members.class));
//        verify(mogakkoRoomTimeRepository, never()).save(any(MogakkoRoomTime.class));
//    }
//
//    @Test
//    void checkEmail() {
//    }
//
//    @Test
//    void checkNickname() {
//    }
//
//    @Test
//    void login() {
//    }
//
//    @Test
//    void logout() {
//    }
//
//    @Test
//    void readMyPage() {
//    }
//
//    @Test
//    void profileUpdate() {
//    }
//
//    @Test
//    void profileDelete() {
//    }
//
//    @Test
//    void getMemberProfile() {
//    }
//
//    @Test
//    void addGithub() {
//    }
//
//    @Test
//    void readBestMembers() {
//    }
//}