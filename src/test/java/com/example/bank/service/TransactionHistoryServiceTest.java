package com.example.bank.service;

import com.example.bank.model.CardEntity;
import com.example.bank.model.TransactionHistoryEntity;
import com.example.bank.repository.TransactionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionHistoryServiceTest {
    @Mock
    private TransactionHistoryRepository repository;
    @InjectMocks
    private TransactionHistoryServiceImpl service;

    @Test
    public void findAllByCardAndTimestampBetween_shouldCallRepository() throws Exception{
        final var card=mock(CardEntity.class);
        final var from=Timestamp.from(Instant.now());
        final var to=Timestamp.from(Instant.now());
        service.findAllByCardAndTimestampBetween(card,from,to);
        verify(repository, times(1)).findAllByCardAndTimestampBetween(card,from,to);
    }

    @Test
    public void save_shouldCallRepository() throws Exception{
        final var transactionHistory=mock(TransactionHistoryEntity.class);
        service.save(transactionHistory);
        verify(repository, times(1)).save(transactionHistory);
    }
}
