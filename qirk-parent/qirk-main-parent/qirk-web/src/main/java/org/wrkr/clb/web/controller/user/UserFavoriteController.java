package org.wrkr.clb.web.controller.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteReadDTO;
import org.wrkr.clb.services.user.UserFavoriteService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "user-favorite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserFavoriteController extends BaseExceptionHandlerController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<UserFavoriteReadDTO, Void> create(HttpSession session,
            @RequestBody UserFavoriteDTO userFavoriteDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        UserFavoriteReadDTO userFavoriteReadDTO = userFavoriteService.create(getSessionUser(session), userFavoriteDTO);
        logProcessingTimeFromStartTime(startTime, "create", userFavoriteDTO.projectId);
        return new JsonContainer<UserFavoriteReadDTO, Void>(userFavoriteReadDTO);
    }

    @PutMapping(value = "move")
    public JsonContainer<UserFavoriteReadDTO, Void> move(HttpSession session,
            @RequestBody MoveDTO moveDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        UserFavoriteReadDTO userFavoriteDTO = userFavoriteService.move(getSessionUser(session), moveDTO);
        logProcessingTimeFromStartTime(startTime, "move", moveDTO.id, moveDTO.previousId);
        return new JsonContainer<UserFavoriteReadDTO, Void>(userFavoriteDTO);
    }

    @GetMapping(value = "list")
    public JsonContainer<UserFavoriteReadDTO, Void> list(HttpSession session) {
        long startTime = System.currentTimeMillis();
        User currentUser = getSessionUser(session);
        List<UserFavoriteReadDTO> userFavoriteDTOList = userFavoriteService.listByUser(currentUser);
        logProcessingTimeFromStartTime(startTime, "list", currentUser);
        return new JsonContainer<UserFavoriteReadDTO, Void>(userFavoriteDTOList);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        userFavoriteService.deleteById(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
