package com.example.back2.service;

import com.example.back2.domain.Todo;
import com.example.back2.dto.PageRequestDTO;
import com.example.back2.dto.PageResponseDTO;
import com.example.back2.dto.TodoDTO;
import com.example.back2.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    public Long register(TodoDTO todoDTO){
        Todo todo = modelMapper.map(todoDTO, Todo.class);

        Todo saveTodo = todoRepository.save(todo);

        return saveTodo.getTno();
    }

    public TodoDTO get(Long tno){
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        TodoDTO dto = modelMapper.map(todo, TodoDTO.class);

        return dto;
    }
    public void modify(TodoDTO todoDTO){
        Optional<Todo> result = todoRepository.findById(todoDTO.getTno());
        Todo todo = result.orElseThrow();

        todo.setTitle(todoDTO.getTitle());
        todo.setWriter(todoDTO.getWriter());
        todo.setComplete(todoDTO.isComplete());

        todoRepository.save(todo);
    }
    public void delete(Long tno){
        todoRepository.deleteById(tno);
    }

    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO){
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        List<TodoDTO> dtoList = result.getContent().stream()
                .map(todo -> modelMapper.map(todo, TodoDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<TodoDTO> responseDTO =
                PageResponseDTO.<TodoDTO>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(totalCount)
                        .build();
        return responseDTO;
    }
}
