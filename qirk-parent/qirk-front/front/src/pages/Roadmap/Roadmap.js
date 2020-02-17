import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { useTranslation } from "react-i18next";

import { DragDropContext, Droppable } from "react-beautiful-dnd";
import { Draggable } from "react-beautiful-dnd";

import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Input,
  InputLabel,
  Button,
  Select,
  MenuItem,
  IconButton,
  Icon
} from "@material-ui/core";

import PrivatePage from "./../../components/PrivatePage/PrivatePage";
import {
  getRoadList,
  postRoad,
  putRoad,
  deleteRoad,
  updateRoadsOrder,
  postCard,
  deleteCard,
  updateCard,
  updateRoadCardsOrder,
  getCardlessTasks,
  setCardlessTasksOrder,
  setCardTasksOrder,
  updateTaskLocally,
  setSearchCardlessFor,
  setFilteredCardlessTasksOrder,
  setRoadCardsOrder
} from "../../actions/roadmapActions";
import constants from "../../utils/constants";
import WithFetch from "../../components/WithFetch/WithFetch";
import Loading from "../../components/Loading/Loading";
import axios from "../../utils/axios";
import endpoints from "../../utils/endpoints";
import { responseIsStatusOk } from "../../utils/variousUtils";
import { blue, grey, green, amber, red } from "@material-ui/core/colors";
import useKeyPress from "../../utils/hooks/useKeyPress";

const ID_NOT_PROVIDED = -42;
const idDelimiter = "_";
const RoadmapAddEditForm = props => {
  const {
    open = false,
    updating = false,
    item,
    item: { name: _name = "", id = ID_NOT_PROVIDED },
    onClose = () => console.error("missing onClose prop at RoadmapAddEditForm"),
    onAdd = () => console.error("missing onAdd prop at RoadmapAddEditForm"),
    onEdit = () => console.error("missing onEdit prop at RoadmapAddEditForm")
  } = props;

  const { t } = useTranslation();

  const createMode = id === ID_NOT_PROVIDED;
  const editMode = !createMode;

  const [name, setName] = useState(_name);

  const handleSubmit = e => {
    e.preventDefault();

    try {
      createMode && onAdd({ name });

      editMode && onEdit({ ...item, id, name });

      onClose();
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    setName(_name);
  }, [_name, props]);

  return (
    <Dialog
      open={open}
      onClose={onClose}
      aria-labelledby="form-dialog-title"
      maxWidth="lg"
    >
      <form onSubmit={handleSubmit}>
        <fieldset
          style={{ border: 0, margin: 0, padding: 0 }}
          disabled={updating}
        >
          <DialogTitle id="form-dialog-title">
            {createMode && t("add_new_roadmap")}
            {editMode && t("update_roadmap")}
          </DialogTitle>
          <DialogContent>
            <FormControl required fullWidth style={{ paddingBottom: 12 }}>
              <InputLabel htmlFor="name">{t("roadmap_name")}</InputLabel>
              <Input
                id="name"
                name="name"
                type="name"
                inputProps={{
                  maxLength: 127
                }}
                autoFocus
                value={name}
                required
                onChange={e => setName(e.target.value)}
              />
            </FormControl>
          </DialogContent>
          <DialogActions>
            <Button type="button" onClick={onClose} color="primary">
              {t("cancel")}
            </Button>
            <Button disabled={updating} type="submit" color="primary">
              {createMode && t("add")}
              {editMode && t("update")}
            </Button>
          </DialogActions>
        </fieldset>
      </form>
    </Dialog>
  );
};

const CardAddForm = props => {
  const {
    open = false,
    onClose = () => console.error("missing onClose prop at CardAddForm"),
    onAdd = () => console.error("missing onAdd prop at CardAddForm")
  } = props;
  const { t } = useTranslation();

  const [name, setName] = useState("");
  const [status, setStatus] = useState(constants.CARD_STATUSES.ACTIVE);

  useEffect(() => {
    setName("");
  }, [props]);

  const handleSubmit = e => {
    e.preventDefault();

    try {
      onAdd({ name, status });

      onClose();
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      aria-labelledby="form-dialog-title"
      maxWidth="lg"
    >
      <form onSubmit={handleSubmit}>
        <fieldset style={{ border: 0, margin: 0, padding: 0 }}>
          <DialogTitle id="form-dialog-title">{t("add_new_card")}</DialogTitle>
          <DialogContent>
            <FormControl
              required
              fullWidth
              margin="normal"
              style={{ marginTop: 0 }}
            >
              <InputLabel htmlFor="name">{t("card_name")}</InputLabel>
              <Input
                id="name"
                name="name"
                type="name"
                autoFocus
                maxLength={127}
                value={name}
                required
                onChange={e => setName(e.target.value)}
              />
            </FormControl>
            <div>
              <FormControl fullWidth margin="normal">
                <div style={{ width: 100 }}>
                  <InputLabel htmlFor="status">{t("status")}</InputLabel>
                  <Select
                    value={status}
                    onChange={e => setStatus(e.target.value)}
                    inputProps={{
                      name: "status",
                      id: "status"
                    }}
                  >
                    {Object.keys(constants.CARD_STATUSES).map(s => (
                      <MenuItem key={s} value={s}>
                        {t(`CardStatuses.${s}`)}
                      </MenuItem>
                    ))}
                  </Select>
                </div>
              </FormControl>
            </div>
          </DialogContent>

          <DialogActions>
            <Button type="button" onClick={onClose} color="primary">
              {t("cancel")}
            </Button>
            <Button type="submit" color="primary">
              {t("add")}
            </Button>
          </DialogActions>
        </fieldset>
      </form>
    </Dialog>
  );
};

const DraggableRoad = ({
  road: r = null,
  index = -1,
  gotCards = false,
  tasksById = {},
  cardsById = {},
  deleteCard,
  updateCard,
  deleteRoad,
  onOpenEditRoadForm = () =>
    console.error("onOpenEditRoadForm prop is not present at DraggableRoad"),
  onOpenCreateCardForm = () =>
    console.error("onOpenCreateCardForm prop is not present at DraggableRoad")
}) => {
  const [open, setOpen] = useState(false);
  const { t } = useTranslation();

  if (!r) {
    return null;
  }

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <Draggable draggableId={`road${idDelimiter}${r.id}`} index={index}>
      {provided => (
        <div /* road */
          ref={provided.innerRef}
          {...provided.dragHandleProps}
          {...provided.draggableProps}
          style={{
            ...provided.draggableProps.style,
            padding: 8,
            paddingLeft: 12,
            borderLeft: `3px solid ${grey[400]}`,
            background: grey[200],
            marginBottom: 16
          }}
        >
          <div style={{ display: "flex" }}>
            <div style={{ flex: 1, display: "flex" }}>
              <span style={{ margin: "auto 0" }}>{r.name}</span>
            </div>
            <div>
              <IconButton
                disabled={r.updating || r.deleting}
                onClick={() => {
                  onOpenEditRoadForm(r);
                }}
              >
                <Icon>edit</Icon>
              </IconButton>
              <IconButton
                onClick={handleClickOpen}
                disabled={r.deleting || r.updating}
              >
                <Icon>delete_forever</Icon>
              </IconButton>
              <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="form-dialog-title"
                maxWidth="lg"
              >
                <DialogTitle id="form-dialog-title">
                  {t("delete_road")} {r.name}
                </DialogTitle>
                <DialogContent>{t("delete_road_confirmation")}</DialogContent>
                <DialogActions>
                  <Button onClick={handleClose} color="primary">
                    {t("cancel")}
                  </Button>
                  <Button
                    disabled={r.deleting || r.updating}
                    color="primary"
                    onClick={() => deleteRoad(r.id)}
                  >
                    {t("Button.Delete")}
                  </Button>
                </DialogActions>
              </Dialog>
              <IconButton onClick={() => onOpenCreateCardForm(r.id)}>
                <Icon>add</Icon>
              </IconButton>
            </div>
          </div>

          <div style={{ overflowX: "auto" }}>
            <CardsDroppable
              road={r}
              gotCards={gotCards}
              tasksById={tasksById}
              cardsById={cardsById}
              deleteCard={deleteCard}
              updateCard={updateCard}
            />
          </div>
        </div>
      )}
    </Draggable>
  );
};

const CardsDroppable = props => {
  const {
    road: r = null,
    gotCards = false,
    tasksById = {},
    cardsById = {},
    deleteCard,
    updateCard
  } = props;

  if (!r) {
    return null;
  }

  return (
    <Droppable
      type={`cards`}
      droppableId={`cardsOfRoad${idDelimiter}${r.id}`}
      direction="horizontal"
    >
      {provided => (
        <div
          /* cards */
          ref={provided.innerRef}
          {...provided.droppableProps}
          style={{
            ...provided.droppableProps.style,
            display: "inline-flex",
            paddingBottom: 16,
            paddingTop: 8,
            minWidth: "100%",
            minHeight: 150
          }}
        >
          {r.cardsOrder && r.cardsOrder.length > 0 && (
            <>
              {r.cardsOrder.map((cId, index) => (
                <CardDraggable
                  key={cId}
                  tasksById={tasksById}
                  card={cardsById[cId]}
                  index={index}
                  roadId={r.id}
                  deleteCard={deleteCard}
                  updateCard={updateCard}
                />
              ))}
            </>
          )}
          {(!r.cardsOrder || r.cardsOrder.length === 0) && (
            <div style={{ padding: 32, paddingLeft: 0, display: "flex" }}>
              <span style={{ fontSize: "14px", margin: "auto" }}>
                {gotCards ? (
                  <>Drag and drop come cards here or click + to create one</>
                ) : (
                  <>Click + to create a card</>
                )}
              </span>
            </div>
          )}
          {provided.placeholder}
        </div>
      )}
    </Droppable>
  );
};

const CardlessTasksDroppable = ({
  cardlessTasksOrder = [],
  tasksById = {},
  filteredMode = false,
  filteredCardlessTasksOrder = []
}) => {
  const droppableId = "cardlessTasks";
  const tasksDraggableIdType = "cardlessTask";

  const tasksOrder = filteredMode
    ? filteredCardlessTasksOrder
    : cardlessTasksOrder;

  return (
    <Droppable droppableId={droppableId} type="tasks">
      {provided => (
        <div
          ref={provided.innerRef}
          {...provided.droppableProps}
          style={{
            ...provided.droppableProps.style,
            width: 234,
            height: "100%"
          }}
        >
          {(!tasksOrder || tasksOrder.length === 0) && (
            <div style={{ padding: 16, fontSize: "15px" }}>No tasks</div>
          )}

          {tasksOrder &&
            tasksOrder.length > 0 &&
            tasksOrder.map((tId, index) => (
              <DraggableTask
                key={tId}
                index={index}
                draggableIdType={tasksDraggableIdType}
                task={tasksById[tId]}
              />
            ))}
          {provided.placeholder}
        </div>
      )}
    </Droppable>
  );
};

const CardTasksDroppable = ({ card: c = null, tasksById = {} }) => {
  const droppableIdType = "cardTasks";
  const tasksDraggableIdType = "cardTask";
  const placeholderMessage = "Drag and drop some tasks here";

  if (!c) {
    return null;
  }

  return (
    <Droppable
      type="tasks"
      droppableId={`${droppableIdType}${idDelimiter}${c.id}`}
      direction="vertical"
    >
      {provided => (
        <div
          /* tasks */ ref={provided.innerRef}
          {...provided.droppableProps}
          style={{
            padding: "0 8px 8px 8px",
            height: "inherit"
          }}
        >
          {c.tasksOrder.map((tId, index) => (
            <DraggableTask
              key={tId}
              task={tasksById[tId]}
              index={index}
              draggableIdType={tasksDraggableIdType}
            />
          ))}
          {c.tasksOrder && c.tasksOrder.length > 0 && (
            <>{provided.placeholder}</>
          )}
          {(!c.tasksOrder || c.tasksOrder.length === 0) && (
            <div style={{ padding: 16, textAlign: "center", fontSize: "14px" }}>
              {placeholderMessage}
            </div>
          )}
        </div>
      )}
    </Droppable>
  );
};

const DraggableTask = ({
  index = -1,
  task: t = null,
  draggableIdType = "cardTask"
}) => {
  if (!t) {
    return null;
  }

  const renderPriority = () => {
    const name_code = t && t.task_priority && t.task_priority.name_code;

    if (!name_code) {
      return null;
    }

    const styles = {
      width: 5
    };

    if (name_code === constants.TASK_PRIORITIES.MAJOR) {
      styles.background = "green";
    } else if (name_code === constants.TASK_PRIORITIES.MINOR) {
      styles.background = "blue";
    } else if (name_code === constants.TASK_PRIORITIES.CRITICAL) {
      styles.background = red[900];
    }

    return <div style={{ ...styles }}> </div>;
  };

  const renderStatus = () => {
    const name_code = t && t.task_status && t.task_status.name_code;

    if (!name_code) {
      return null;
    }

    const styles = {
      width: 5
    };

    if (name_code === constants.TASK_STATUSES.OPEN) {
      styles.background = constants.TASKS_COLORS.TASK_STATUSES_COLORS.OPEN;
    } else if (name_code === constants.TASK_STATUSES.CLOSED) {
      styles.background = constants.TASKS_COLORS.TASK_STATUSES_COLORS.CLOSED;
    } else if (name_code === constants.TASK_STATUSES.WAITING_FOR_QA) {
      styles.background =
        constants.TASKS_COLORS.TASK_STATUSES_COLORS.WAITING_FOR_QA;
    } else if (name_code === constants.TASK_STATUSES.IN_QA_REVIEW) {
      styles.background =
        constants.TASKS_COLORS.TASK_STATUSES_COLORS.IN_QA_REVIEW;
    } else if (name_code === constants.TASK_STATUSES.REJECTED) {
      styles.background = constants.TASKS_COLORS.TASK_STATUSES_COLORS.REJECTED;
    } else if (name_code === constants.TASK_STATUSES.IN_DEVELOPMENT) {
      styles.background =
        constants.TASKS_COLORS.TASK_STATUSES_COLORS.IN_DEVELOPMENT;
    }

    return <div style={{ ...styles }}> </div>;
  };

  return (
    <Draggable
      draggableId={`${draggableIdType}${idDelimiter}${t.id}`}
      index={index}
    >
      {provided => (
        <div
          /* task */
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          style={{
            ...provided.draggableProps.style,
            marginBottom: 8
          }}
        >
          <div
            style={{
              display: "flex",
              width: "100%",
              borderRadius: 5,
              overflow: "hidden",
              background: grey[200],
              boxShadow: "#b3b3b3 1px 1px 1px 0px"
            }}
          >
            {renderPriority()}
            <div
              style={{
                padding: 8,
                flex: 1,
                wordBreak: "break-word",
                fontSize: "14px"
              }}
            >
              {t.uiSummary}
            </div>
            {renderStatus()}
          </div>
        </div>
      )}
    </Draggable>
  );
};

const CardDraggable = ({
  card: c = null,
  index = -1,
  tasksById = {},
  roadId = -1,
  updateCard = () => console.error("check CardDraggable updateCard prop"),
  deleteCard = () => console.error("check CardDraggable deleteCard prop")
}) => {
  const [tmpCardName, setTmpCardName] = useState(null);
  const [editing, setEditing] = useState(false);

  const enterPressed = useKeyPress("Enter");

  const lightColor = grey[50];

  const cardHeaderStyles = {
    color: lightColor
  };

  if (c.status === constants.CARD_STATUSES.COMPLETED) {
    cardHeaderStyles.background = blue[600];
  } else if (c.status === constants.CARD_STATUSES.PAUSED) {
    cardHeaderStyles.background = amber[600];
  } else if (c.status === constants.CARD_STATUSES.ACTIVE) {
    cardHeaderStyles.background = green[600];
  } else if (c.status === constants.CARD_STATUSES.STOPPED) {
    cardHeaderStyles.background = grey[600];
  }

  const [open, setOpen] = useState(false);

  const {t}=useTranslation()

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  useEffect(() => {
    if (editing && enterPressed) {
      if (c.name !== tmpCardName) {
        updateCard({
          ...c,
          name: tmpCardName
        });
      }

      setEditing(false);
    }
  }, [enterPressed, editing]);
  if (!c) {
    return null;
  }
  return (
    <Draggable draggableId={`card${idDelimiter}${c.id}`} index={index}>
      {provided => (
        <div
          /* card */
          ref={provided.innerRef}
          {...provided.dragHandleProps}
          {...provided.draggableProps}
          style={{
            ...provided.draggableProps.style,
            background: "white",
            boxShadow: "1px 1px 5px -1px grey",
            width: 250,
            marginRight: 16,
            borderRadius: 5,
            overflow: "hidden"
          }}
        >
          <div /* card header */
            style={{
              ...cardHeaderStyles,
              display: "flex",
              padding: 4,
              paddingLeft: 8,
              wordBreak: "break-word"
            }}
          >
            {editing && (
              <>
                <div style={{ flex: 1, display: "flex" }}>
                  <input
                    disabled={c.updating}
                    autoFocus={true}
                    maxLength={127}
                    style={{
                      width: 137,
                      margin: "auto 0",
                      outline: "none",
                      border: "0px solid pink",
                      background: "transparent",
                      color: lightColor,
                      borderBottom: `2px solid ${lightColor}`
                    }}
                    value={tmpCardName || c.name}
                    onChange={e => setTmpCardName(e.target.value)}
                  />
                </div>
                <div>
                  <IconButton
                    onClick={e => {
                      e.preventDefault(e);

                      if (c.name !== tmpCardName) {
                        updateCard({
                          ...c,
                          name: tmpCardName
                        });
                      }

                      setEditing(false);
                    }}
                    disabled={c.updating}
                    style={{ color: lightColor }}
                  >
                    <Icon>check</Icon>
                  </IconButton>
                  <IconButton
                    onClick={e => {
                      e.preventDefault(e);
                      setEditing(false);
                      setTmpCardName(c.name);
                    }}
                    disabled={c.updating}
                    style={{ color: lightColor }}
                  >
                    <Icon>undo</Icon>
                  </IconButton>
                </div>
              </>
            )}
            {!editing && (
              <>
                <div
                  style={{
                    flex: 1,
                    display: "flex"
                  }}
                >
                  <span
                    style={{
                      margin: "auto 0"
                    }}
                  >
                    {c.name}
                  </span>{" "}
                </div>
                <div>
                  <IconButton
                    disabled={c.updating || c.deleting}
                    onClick={e => {
                      e.preventDefault(e);
                      if (!tmpCardName) {
                        setTmpCardName(c.name);
                      }
                      setEditing(true);
                    }}
                    style={{ color: lightColor }}
                  >
                    <Icon>edit</Icon>
                  </IconButton>
                </div>
              </>
            )}
          </div>

          <div
            /* card action buttons */ style={{
              display: "flex",
              padding: "4px 0px",
              justifyContent: "center"
            }}
          >
            <IconButton
              disabled={
                editing ||
                c.updating ||
                c.deleting ||
                c.status === constants.CARD_STATUSES.COMPLETED
              }
              onClick={e => {
                e.preventDefault();
                updateCard({
                  ...c,
                  status: constants.CARD_STATUSES.COMPLETED
                });
              }}
            >
              <Icon>done</Icon>
            </IconButton>
            <IconButton
              disabled={
                editing ||
                c.updating ||
                c.deleting ||
                c.status === constants.CARD_STATUSES.ACTIVE
              }
              onClick={e => {
                e.preventDefault();
                updateCard({
                  ...c,
                  status: constants.CARD_STATUSES.ACTIVE
                });
              }}
            >
              <Icon>play_arrow</Icon>
            </IconButton>

            <IconButton
              disabled={
                editing ||
                c.updating ||
                c.deleting ||
                c.status === constants.CARD_STATUSES.PAUSED
              }
              onClick={e => {
                e.preventDefault();
                updateCard({
                  ...c,
                  status: constants.CARD_STATUSES.PAUSED
                });
              }}
            >
              <Icon>pause</Icon>
            </IconButton>
            <IconButton
              disabled={
                editing ||
                c.updating ||
                c.deleting ||
                c.status === constants.CARD_STATUSES.STOPPED
              }
              onClick={e => {
                e.preventDefault();
                updateCard({
                  ...c,
                  status: constants.CARD_STATUSES.STOPPED
                });
              }}
            >
              <Icon>stop</Icon>
            </IconButton>
            <IconButton
              disabled={editing || c.updating || c.deleting}
              onClick={handleClickOpen}
            >
              <Icon>delete_forever</Icon>
            </IconButton>
            <Dialog
              open={open}
              onClose={handleClose}
              aria-labelledby="form-dialog-title"
              maxWidth="lg"
            >
              <DialogTitle id="form-dialog-title">
                {t("delete_card")} {c.name}
              </DialogTitle>
              <DialogContent>{t("delete_card_confirmation")}</DialogContent>
              <DialogActions>
                <Button onClick={handleClose} color="primary">
                  {t("cancel")}
                </Button>
                <Button
                  disabled={editing || c.updating || c.deleting}
                  color="primary"
                  onClick={e => {
                    e.preventDefault();
                    deleteCard(roadId, c.id);
                  }}
                >
                  {t("Button.Delete")}
                </Button>
              </DialogActions>
            </Dialog>
          </div>

          <div style={{ height: "100%" }} /* card tasks */>
            <CardTasksDroppable card={c} tasksById={tasksById} />
          </div>
        </div>
      )}
    </Draggable>
  );
};

const RoadsDroppable = ({
  roadsOrder = [],
  roadsById = {},
  tasksById = {},
  cardsById = {},
  deleteCard,
  updateCard,
  deleteRoad,
  onOpenEditRoadForm = () =>
    console.error("onOpenEditRoadForm prop is not present at RoadsDroppable"),

  onOpenCreateCardForm = () =>
    console.error("onOpenCreateCardForm prop is not present at RoadsDroppable")
}) => {
  return (
    <Droppable droppableId="roadDropzone" type="roads">
      {provided => (
        <div /* roads */ ref={provided.innerRef} {...provided.droppableProps}>
          {roadsOrder.map((rId, index) => {
            return (
              <DraggableRoad
                key={rId}
                road={roadsById[rId]}
                index={index}
                gotCards={roadsOrder.some(id => {
                  const road = roadsById[id];

                  if (!road) {
                    return false;
                  }

                  return road.cardsOrder && road.cardsOrder.length > 0;
                })}
                tasksById={tasksById}
                cardsById={cardsById}
                deleteCard={deleteCard}
                updateCard={updateCard}
                deleteRoad={deleteRoad}
                onOpenEditRoadForm={onOpenEditRoadForm}
                onOpenCreateCardForm={onOpenCreateCardForm}
              />
            );
          })}
          {provided.placeholder}
        </div>
      )}
    </Droppable>
  );
};

const Roadmap = props => {
  const {
    loadingProject,
    loadingRoadList,
    cardsById,
    roadsById,
    roadsOrder,
    project,
    updateRoadsOrder,
    getRoadList,
    getCardlessTasks,
    postRoad,
    putRoad,
    deleteRoad,
    postCard,
    deleteCard,
    updateCard,
    updateRoadCardsOrder,
    setRoadCardsOrder,
    tasksById,
    cardlessTasksOrder,
    loadingCardlessTasks,
    setCardlessTasksOrder,
    setCardTasksOrder,
    updateTaskLocally,
    searchCardlessFor,
    filteredCardlessTasksOrder,
    filteredCardlessTasksMode,
    setSearchCardlessFor,
    setFilteredCardlessTasksOrder,
    match: {
      params: { project_uiid: urlProjectUiId }
    }
  } = props;

  const {t}=useTranslation()

  const { id: projectId } = project;

  const [roadFormOpened, setRoadFormOpened] = useState(false);
  const [selectedRoad, setSelectedRoad] = useState({});
  const [cardFormOpened, setCardFormOpened] = useState(false);
  const [
    currentRoadIdForCardCreation,
    setCurrentRoadIdForCardCreation
  ] = useState(-999);

  const putTaskSlashCard = async putData => {
    try {
      const response = await axios.put(endpoints.PUT_TASK_SLASH_CARD, {
        ...putData
      });

      if (responseIsStatusOk(response)) {
        const data =
          response &&
          response.data &&
          response.data.data &&
          response.data.data.length > 0 &&
          response.data.data[0];

        if (data) {
          const { id, record_version } = data;
          updateTaskLocally({ id, record_version });
        }
      }
    } catch (e) {
      console.error(e);
    }
  };

  const onDragEnd = result => {
    const { destination, source, draggableId } = result;
    if (!destination) {
      return;
    }
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    ) {
      return;
    }

    const splittedDraggable = draggableId.split(idDelimiter);

    const [draggableType, draggableIdValue] = splittedDraggable;

    let newOrder = [];

    if (
      source.droppableId.startsWith("cardTasks") &&
      destination.droppableId.startsWith("cardTasks")
    ) {
      const splittedSourceDroppableId = source.droppableId.split(idDelimiter);
      const sourceCardId = splittedSourceDroppableId[1];

      const splittedDestinationDroppableId = destination.droppableId.split(
        idDelimiter
      );
      const destinationCardId = splittedDestinationDroppableId[1];

      if (sourceCardId === destinationCardId) {
        // moving tasks within a card
        newOrder = [...cardsById[sourceCardId].tasksOrder];
        newOrder.splice(source.index, 1);
        newOrder.splice(destination.index, 0, draggableIdValue);
        setCardTasksOrder(sourceCardId, newOrder);
      } else {
        // moving tasks from card to card
        const sourceOrder = [...cardsById[sourceCardId].tasksOrder];
        sourceOrder.splice(source.index, 1);

        const destinationOrder = [...cardsById[destinationCardId].tasksOrder];
        destinationOrder.splice(destination.index, 0, draggableIdValue);

        setCardTasksOrder(sourceCardId, sourceOrder);
        setCardTasksOrder(destinationCardId, destinationOrder);
      }
    } else if (
      source.droppableId === "cardlessTasks" &&
      destination.droppableId.startsWith("cardTasks")
    ) {
      // task moved from cardless to card
      const task = tasksById[draggableIdValue];

      if (!task) {
        return;
      }

      const splittedDestinationDroppableId = destination.droppableId.split(
        idDelimiter
      );
      const destinationCardId = splittedDestinationDroppableId[1];

      newOrder = [...cardlessTasksOrder];
      newOrder.splice(source.index, 1);
      setCardlessTasksOrder(newOrder);

      const destinationOrder = [...cardsById[destinationCardId].tasksOrder];
      destinationOrder.splice(destination.index, 0, draggableIdValue);
      setCardTasksOrder(destinationCardId, destinationOrder);

      putTaskSlashCard({
        id: task.id,
        record_version: task.record_version,
        card: destinationCardId
      });
    } else if (
      source.droppableId.startsWith("cardTasks") &&
      destination.droppableId === "cardlessTasks"
    ) {
      // task moved from card to cardless
      const task = tasksById[draggableIdValue];

      if (!task) {
        return;
      }

      const splittedSourceDroppableId = source.droppableId.split(idDelimiter);
      const sourceCardId = splittedSourceDroppableId[1];

      const sourceOrder = [...cardsById[sourceCardId].tasksOrder];
      sourceOrder.splice(source.index, 1);
      setCardTasksOrder(sourceCardId, sourceOrder);

      const newOrder = [...cardlessTasksOrder];
      newOrder.splice(destination.index, 0, draggableIdValue);
      setCardlessTasksOrder(newOrder);

      putTaskSlashCard({
        id: task.id,
        record_version: task.record_version,
        card: null
      });
    } else if (draggableType === "road") {
      newOrder = [...roadsOrder];

      newOrder.splice(source.index, 1);
      newOrder.splice(destination.index, 0, Number(draggableIdValue));

      updateRoadsOrder(
        newOrder,
        /* putData = */ {
          id: draggableIdValue,

          previous: newOrder[destination.index - 1] || null,
          next: newOrder[destination.index + 1] || null,

          old_previous: roadsOrder[source.index - 1] || null,
          old_next: roadsOrder[source.index + 1] || null
        }
      );
    } else if (
      draggableType === "card" &&
      destination.droppableId !== source.droppableId
    ) {
      const cardId = Number(draggableIdValue);
      const destinationRoadId = Number(
        destination.droppableId.split(idDelimiter)[1]
      );
      const sourceRoadId = Number(source.droppableId.split(idDelimiter)[1]);

      const oldSourceOrder = [...roadsById[sourceRoadId].cardsOrder];
      const newSourceOrder = [...oldSourceOrder];

      const oldDestinationOrder = roadsById[destinationRoadId].cardsOrder
        ? [...roadsById[destinationRoadId].cardsOrder]
        : [];
      const newDestinationOrder = [...oldDestinationOrder];

      newSourceOrder.splice(source.index, 1);
      newDestinationOrder.splice(destination.index, 0, cardId);

      updateRoadCardsOrder(
        destinationRoadId,
        newDestinationOrder,
        /* putData = */ {
          id: cardId,

          previous: newDestinationOrder[destination.index - 1] || null,
          next: newDestinationOrder[destination.index + 1] || null,

          old_previous: oldSourceOrder[source.index - 1] || null,
          old_next: oldSourceOrder[source.index + 1] || null,

          road: destinationRoadId
        }
      );

      setRoadCardsOrder(sourceRoadId, newSourceOrder);
    } else if (
      draggableType === "card" &&
      destination.droppableId === source.droppableId
    ) {
      const cardId = Number(draggableIdValue);
      const roadId = Number(destination.droppableId.split(idDelimiter)[1]);

      const oldOrder = [...roadsById[roadId].cardsOrder];
      newOrder = [...oldOrder];

      newOrder.splice(source.index, 1);
      newOrder.splice(destination.index, 0, cardId);

      updateRoadCardsOrder(
        roadId,
        newOrder,
        /* putData = */ {
          id: cardId,

          previous: newOrder[destination.index - 1] || null,
          next: newOrder[destination.index + 1] || null,

          old_previous: oldOrder[source.index - 1] || null,
          old_next: oldOrder[source.index + 1] || null
        }
      );
    } else if (draggableType === "cardlessTask") {
      const taskId = Number(draggableIdValue);

      let oldOrder = [];

      if (filteredCardlessTasksMode) {
        oldOrder = filteredCardlessTasksOrder;
        newOrder = [...oldOrder];

        newOrder.splice(source.index, 1);
        newOrder.splice(destination.index, 0, taskId);

        setFilteredCardlessTasksOrder(newOrder);
      } else {
        oldOrder = cardlessTasksOrder;
        newOrder = [...oldOrder];

        newOrder.splice(source.index, 1);
        newOrder.splice(destination.index, 0, taskId);

        setCardlessTasksOrder(newOrder);
      }
    }
  };

  useEffect(() => {
    if (filteredCardlessTasksMode) {
      setFilteredCardlessTasksOrder(
        cardlessTasksOrder.filter(tId => {
          const task = tasksById[tId];

          if (!task) {
            return false;
          }

          return task.uiSummary
            .toLowerCase()
            .includes(searchCardlessFor.toLowerCase());
        })
      );
    }
  }, [searchCardlessFor, filteredCardlessTasksMode]);

  useEffect(() => {
    if (urlProjectUiId) {
      getRoadList(urlProjectUiId);
      getCardlessTasks(urlProjectUiId);
    }
  }, [urlProjectUiId]);

  const loadingSomething =
    loadingRoadList || loadingProject || loadingCardlessTasks;

  const gotRoadmaps = roadsOrder && roadsOrder.length > 0;

  const noRoadmaps = !gotRoadmaps;

  return (
    <PrivatePage>
      <WithFetch fetchList={[constants.FETCH_PROJECT]}>
        <CardAddForm
          open={cardFormOpened}
          onAdd={card =>
            postCard({ ...card, road: currentRoadIdForCardCreation })
          }
          onClose={() => setCardFormOpened(false)}
        />
        <RoadmapAddEditForm
          open={roadFormOpened}
          item={selectedRoad}
          updating={false}
          onAdd={roadInfo => postRoad({ ...roadInfo, project: projectId })}
          onEdit={putRoad}
          onClose={() => setRoadFormOpened(false)}
        />

        <h1>{t("roadmap")}</h1>

        {loadingSomething && <Loading />}

        {!loadingSomething && (
          <DragDropContext onDragEnd={onDragEnd}>
            <div style={{ display: "flex" }}>
              <div
                /* cardless tasks */ style={{
                  paddingRight: 30,
                  boxSizing: "border-box"
                }}
              >
                <div style={{ display: "flex", marginBottom: 8 }}>
                  <div style={{ flex: 1 }}>
                    <input
                      style={{
                        height: 48,
                        width: "100%",
                        border: "0px solid pink",
                        borderBottom: `2px solid ${grey[700]}`,
                        outline: "none"
                      }}
                      placeholder={"Search"}
                      type="text"
                      value={searchCardlessFor}
                      onChange={e => setSearchCardlessFor(e.target.value)}
                    />
                  </div>
                  {filteredCardlessTasksMode && (
                    <div>
                      <IconButton
                        style={{ marginLeft: 4 }}
                        onClick={() => setSearchCardlessFor("")}
                      >
                        <Icon>clear</Icon>
                      </IconButton>
                    </div>
                  )}
                </div>
                <CardlessTasksDroppable
                  tasksById={tasksById}
                  searchCardlessFor={searchCardlessFor}
                  filteredMode={filteredCardlessTasksMode}
                  filteredCardlessTasksOrder={filteredCardlessTasksOrder}
                  cardlessTasksOrder={cardlessTasksOrder}
                />
              </div>

              <div
                /* roads */ style={{
                  flex: 1,
                  width: "calc(100% - 265px)",
                  maxWidth: "calc(100% - 165px)"
                }}
              >
                {gotRoadmaps && (
                  <RoadsDroppable
                    roadsOrder={roadsOrder}
                    roadsById={roadsById}
                    tasksById={tasksById}
                    cardsById={cardsById}
                    deleteCard={deleteCard}
                    updateCard={updateCard}
                    deleteRoad={deleteRoad}
                    onOpenEditRoadForm={road => {
                      setSelectedRoad(road);
                      setRoadFormOpened(true);
                    }}
                    onOpenCreateCardForm={roadId => {
                      setCurrentRoadIdForCardCreation(roadId);
                      setCardFormOpened(true);
                    }}
                  />
                )}

                <div style={{ display: "flex" }}>
                  <IconButton
                    style={{
                      margin: "auto",
                      background: "#eee",
                      width: 300,
                      borderRadius: 5
                    }}
                    onClick={() => {
                      setSelectedRoad({});
                      setRoadFormOpened(true);
                    }}
                  >
                    <Icon>add</Icon>
                  </IconButton>
                </div>

                {noRoadmaps && (
                  <div style={{ display: "flex", padding: 32 }}>
                    <span style={{ margin: "auto" }}>
                      {t("no_roadmaps_found")}
                    </span>
                  </div>
                )}
              </div>
            </div>
          </DragDropContext>
        )}
      </WithFetch>
    </PrivatePage>
  );
};

export default withRouter(
  connect(
    state => ({
      loadingProject: state.project.fetchStatus === constants.WAITING,
      loadingRoadList: state.roadmap.roadsByIdFetchStatus === constants.WAITING,
      roadsById: state.roadmap.roadsById,
      roadsOrder: state.roadmap.roadsOrder,
      project: state.project.info || {},
      cardsById: state.roadmap.cardsById,
      tasksById: state.roadmap.tasksById,
      cardlessTasksOrder: state.roadmap.cardlessTasksOrder,
      loadingCardlessTasks: state.roadmap.loadingCardlessTasks,
      searchCardlessFor: state.roadmap.searchCardlessFor,
      filteredCardlessTasksOrder: state.roadmap.filteredCardlessTasksOrder,
      filteredCardlessTasksMode: !!state.roadmap.searchCardlessFor
    }),
    {
      getRoadList,
      getCardlessTasks,
      postRoad,
      putRoad,
      deleteRoad,
      updateRoadsOrder,
      postCard,
      deleteCard,
      updateCard,
      updateRoadCardsOrder,
      setRoadCardsOrder,
      setCardlessTasksOrder,
      setCardTasksOrder,
      updateTaskLocally,
      setSearchCardlessFor,
      setFilteredCardlessTasksOrder
    }
  )(Roadmap)
);
