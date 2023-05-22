function drawBoard(boardData) {
    function clearBoard() {
        const existingPits = document.querySelectorAll('.pit');
        const existingMancalas = document.querySelectorAll('.mancala');
        existingPits.forEach(pit => pit.remove());
        existingMancalas.forEach(pit => pit.remove());
    }

    clearBoard();

    const boardElement = document.createElement('div');

    boardElement.className = 'board';

    const players = boardData.players;

    const lowerPlayerId = findLowestPlayerId(boardData.players);
    const lowerPlayer = players.find(player => player.id === lowerPlayerId);
    const upperPlayer = players.find(player => player.id !== lowerPlayerId);


    if (lowerPlayer != null) {
        const lowerPlayerEl = document.querySelector('.upper-player');
        lowerPlayerEl.textContent = `This side of the board belongs to the player with ID  ${lowerPlayer.id}`;
    }

    if (upperPlayer != null) {
        const upperPlayerEl = document.querySelector('.lower-player');
        upperPlayerEl.textContent = `This side of the board belongs to the player with ID  ${upperPlayer.id}`;
    }

    if (lowerPlayer != null && upperPlayer != null) {
        const turn = document.querySelector('.turn');
        if (lowerPlayer.isHisTurn) {
            turn.textContent = `Player ID turn: ${lowerPlayer.id}`;
        } else {
            turn.textContent = `Player ID turn: ${upperPlayer.id}`;
        }

    }

    const mancalaForLowerPlayerID = findMancalaForPlayer(boardData.board.pits, lowerPlayerId);
    const MancalaPitForPlayerOnTheBottom = findFirstMancalaAfterPit(boardData.board.pits, mancalaForLowerPlayerID);
    drawPits(boardData.board.pits, MancalaPitForPlayerOnTheBottom);
}

function findMancalaForPlayer(pits, playerId) {
    // Initialize variables for the player's mancala ID and the current pit
    let playerMancalaId = null;
    let currentPit = pits[0];

    // Loop through the pits until we find a mancala for the given player ID or reach the end of the loop
    while (currentPit && !playerMancalaId) {
        // If we've found a mancala for the given player ID, return its ID
        if (currentPit.type === 'MANCALA' && currentPit.playerId === playerId) {
            playerMancalaId = currentPit.id;
        }

        // Move to the next pit
        currentPit = pits.find(p => p.id === currentPit.nextPitId);
    }

    return playerMancalaId;
}

function findLowestPlayerId(players) {
    let lowestId = null;

    players.forEach(player => {
        if (lowestId === null || player.id < lowestId) {
            lowestId = player.id;
        }
    });

    return lowestId;
}

function findFirstMancalaAfterPit(pits, pitId) {
    // Find the pit with the given ID
    const startingPit = pits.find(pit => pit.id === pitId);

    // Traverse the list of pits starting from the starting pit until we find a mancala pit
    let currentPit = startingPit;
    while (true) {
        currentPit = pits.find(pit => pit.id === currentPit.nextPitId);

        if (currentPit.type === 'MANCALA') {
            return currentPit.id;
        }

        if (currentPit.id === startingPit.id) {
            // We've looped around to the starting pit without finding a mancala pit
            return null;
        }
    }
}

function drawPits(pits, otherPlayerMancalaId) {
    const lastPit = drawUpperPlayerPits(pits, otherPlayerMancalaId);
    drawLowerPlayerPits(pits, lastPit.id);
}

function drawUpperPlayerPits(pits, otherPlayerMancalaId) {
    const mancalaPit = pits.find(pit => pit.id === otherPlayerMancalaId);
    let currentPit = pits.find(pit => pit.id === mancalaPit.nextPitId);

    while (currentPit.type === 'NORMAL') {
        // draw current pit on the board according to its type and number of stones
        drawPit(currentPit, true);
        currentPit = pits.find(pit => pit.id === currentPit.nextPitId);
    }

    // draw mancala pit on the board according to its type and number of stones
    drawMancala('upper', currentPit);
    return currentPit;
}


function drawLowerPlayerPits(pits, otherPlayerMancalaId) {
    const mancalaPit = pits.find(pit => pit.id === otherPlayerMancalaId);
    let currentPit = pits.find(pit => pit.id === mancalaPit.nextPitId);

    while (currentPit.type === 'NORMAL') {
        // draw current pit on the board according to its type and number of stones
        drawPit(currentPit, false);
        currentPit = pits.find(pit => pit.id === currentPit.nextPitId);
    }

    // draw mancala pit on the board according to its type and number of stones
    drawMancala('lower', currentPit);

}


function drawPit(pit, isUpper) {
    const pitElement = document.createElement('div');
    pitElement.className = 'pit';

    // Create stones
    const stonesElement = document.createElement('div');

    pitElement.appendChild(stonesElement);
    pitElement.setAttribute('data-pit-id', pit.id);
    pitElement.textContent = pit.stones;

    // Position pit based on isUpper
    const shift = isUpper ? -1 : 1;
    pitElement.style.transform = `translateY(${shift * 50}px)`;


    pitElement.addEventListener('mouseenter', function () {
        pitElement.classList.add('hover');
    });
    pitElement.addEventListener('mouseleave', function () {
        pitElement.classList.remove('hover');
    });

    // Add click listeners to pit element
    pitElement.addEventListener('mousedown', () => {
        pitElement.style.backgroundColor = 'gray';
    });
    pitElement.addEventListener('mouseup', () => {
        pitElement.style.backgroundColor = '';
    });


    // Add click listeners to pit element
    pitElement.addEventListener('click', async () => {
        const params = new URLSearchParams(window.location.search);
        const gameId = params.get('gameId');
        try {
            const response = await fetch(`http://localhost:8080/mancala/sow?gameId=${gameId}&pitId=${pit.id}`, {
                withCredentials: true,
                method: 'POST',
                credentials: 'include'
            });
            const data = await response.json();
            if (response.status === 400) {
                const errorMessage = data.message;
                alert(errorMessage);
                throw new Error(errorMessage);
            }
            drawBoard(data);
        } catch (error) {
            console.error('Error making move:', error);
        }
    });


    // Add pit to canvas
    const board = document.querySelector(`.board[data-position="${isUpper ? 'upper' : 'lower'}"]`);
    board.appendChild(pitElement);
}

function drawMancala(side, pit) {
    const boardEl = document.querySelector(`.board[data-position="${side}"]`);
    if (!boardEl) {
        console.error(`Board element with position "${side}" not found.`);
        return;
    }
    const mancalaEl = document.createElement('div');
    if (side === 'lower') {
        mancalaEl.style.cssFloat = 'right';
    }

    mancalaEl.classList.add('mancala');
    mancalaEl.setAttribute('data-pit-id', pit.id);
    mancalaEl.textContent = pit.stones;

    if (side === 'left') {
        boardEl.insertAdjacentElement('beforebegin', mancalaEl);
    } else {
        boardEl.insertAdjacentElement('afterend', mancalaEl);
    }
}

window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const gameId = urlParams.get('gameId');

    const gameIdEl = document.querySelector('.game_id');
    gameIdEl.textContent = `Game ID: ${gameId}`;

    fetch(`http://localhost:8080/mancala/info?gameId=${gameId}`, {
        withCredentials: true,
        credentials: 'include'
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            const gameStatusEl = document.querySelector('.game_status');
            gameStatusEl.textContent = `Game status: ${data.info.status}`;

            const gameWinnerEl = document.querySelector('.game_winner');
            const winner = data.info.winner;
            const draw = data.info.draw;

            if (winner !== null) {
                gameWinnerEl.textContent = `Game winner: ${winner.id}`;
            } else if (draw) {
                gameWinnerEl.textContent = "Game ended in a draw";
            }
            return data
        })
        .then(data => {
            // call the drawBoard function with the JSON data
            drawBoard(data);
        })
        .catch(error => {
            console.error('Error fetching JSON data:', error);
        });
};

setInterval(() => {
    const params = new URLSearchParams(window.location.search);
    const gameId = params.get('gameId');
    // Use the gameId parameter in the fetch call
    fetch(`http://localhost:8080/mancala/info?gameId=${gameId}`, {
        withCredentials: true,
        credentials: 'include'
    })
        .then(response => {
            return response.json();
        })
        .then(data => {
            // call the drawBoard function with the JSON data
            drawBoard(data);
        })
        .catch(error => {
            console.error('Error fetching JSON data:', error);
        });
}, 1000);
