<script>
  import { onMount, onDestroy } from 'svelte';

  // Canvas element references
  let trailsCanvasEl;
  let mainCanvasEl;
  let stageContainerEl; // For resize handling

  // Adapted JavaScript from firework.js
  // Variables and functions will be defined within this component's scope.

  // Constants and Configuration (adapted)
  const IS_MOBILE = typeof window !== 'undefined' ? window.innerWidth <= 640 : false;
  const IS_DESKTOP = typeof window !== 'undefined' ? window.innerWidth > 800 : false;
  const IS_HEADER = false; // Assuming not used as a header

  const IS_HIGH_END_DEVICE = (() => {
    if (typeof navigator === 'undefined' || !navigator.hardwareConcurrency) {
      return false;
    }
    const hwConcurrency = navigator.hardwareConcurrency;
    const minCount = (typeof window !== 'undefined' && window.innerWidth <= 1024) ? 4 : 8;
    return hwConcurrency >= minCount;
  })();

  const MAX_WIDTH = 7680;
  const MAX_HEIGHT = 4320;
  const GRAVITY = 0.9;
  let simSpeed = 1;

  function getDefaultScaleFactor() {
    if (IS_MOBILE) return 0.9;
    if (IS_HEADER) return 0.75;
    return 1;
  }

  let stageW, stageH;

  let quality = IS_HIGH_END_DEVICE ? 3 : 2;
  let isLowQuality = quality === 1;
  let isNormalQuality = quality === 2;
  let isHighQuality = quality === 3;

  const QUALITY_LOW = 1;
  const QUALITY_NORMAL = 2;
  const QUALITY_HIGH = 3;

  const SKY_LIGHT_NONE = 0;

  const COLOR = {
    Red: '#ff0043',
    Green: '#14fc56',
    Blue: '#1e7fff',
    Purple: '#e60aff',
    Gold: '#ffbf36',
    White: '#ffffff'
  };
  const INVISIBLE = '_INVISIBLE_';

  const PI_2 = Math.PI * 2;
  const PI_HALF = Math.PI * 0.5;

  let trailsStage;
  let mainStage;
  let stages = [];
  
  const store = {
    _listeners: new Set(),
    _dispatch(prevState) {
      this._listeners.forEach((listener) => listener(this.state, prevState))
    },
    state: {
      paused: false,
      config: {
        quality: String(quality),
        shell: 'Random',
        size: IS_DESKTOP ? '3' : '2',
        autoLaunch: true,
        finale: false,
        skyLighting: String(SKY_LIGHT_NONE),
        longExposure: false,
        scaleFactor: getDefaultScaleFactor()
      }
    },
    setState(nextState) {
      const prevState = this.state;
      this.state = Object.assign({}, this.state, nextState);
      this._dispatch(prevState);
    },
    subscribe(listener) {
      this._listeners.add(listener);
      return () => this._listeners.delete(listener);
    },
  };

  function togglePause(toggle) {
    const paused = store.state.paused;
    let newValue = typeof toggle === 'boolean' ? toggle : !paused;
    if (paused !== newValue) {
      store.setState({ paused: newValue });
    }
  }
  
  function configDidUpdate() {
    const config = store.state.config;
    quality = +config.quality;
    isLowQuality = quality === QUALITY_LOW;
    isNormalQuality = quality === QUALITY_NORMAL;
    isHighQuality = quality === QUALITY_HIGH;
    Spark.drawWidth = quality === QUALITY_HIGH ? 0.75 : 1;
  }

  const isRunning = (state = store.state) => !state.paused;
  const qualitySelector = () => +store.state.config.quality;
  const shellNameSelector = () => store.state.config.shell;
  const shellSizeSelector = () => +store.state.config.size;
  const finaleSelector = () => store.state.config.finale;
  const skyLightingSelector = () => +store.state.config.skyLighting;
  const scaleFactorSelector = () => store.state.config.scaleFactor;

  const COLOR_NAMES = Object.keys(COLOR);
  const COLOR_CODES = COLOR_NAMES.map(colorName => COLOR[colorName]);
  const COLOR_CODES_W_INVIS = [...COLOR_CODES, INVISIBLE];
  const COLOR_CODE_INDEXES = COLOR_CODES_W_INVIS.reduce((obj, code, i) => {
    obj[code] = i;
    return obj;
  }, {});
  const COLOR_TUPLES = {};
  COLOR_CODES.forEach(hex => {
    COLOR_TUPLES[hex] = {
      r: parseInt(hex.substr(1, 2), 16),
      g: parseInt(hex.substr(3, 2), 16),
      b: parseInt(hex.substr(5, 2), 16),
    };
  });

  function randomColorSimple() {
    return COLOR_CODES[Math.random() * COLOR_CODES.length | 0];
  }

  let lastColor;
  function randomColor(options) {
    const notSame = options && options.notSame;
    const notColor = options && options.notColor;
    const limitWhite = options && options.limitWhite;
    let color = randomColorSimple();

    if (limitWhite && color === COLOR.White && Math.random() < 0.6) {
      color = randomColorSimple();
    }
    if (notSame) {
      while (color === lastColor) {
        color = randomColorSimple();
      }
    } else if (notColor) {
      while (color === notColor) {
        color = randomColorSimple();
      }
    }
    lastColor = color;
    return color;
  }

  function whiteOrGold() {
    return Math.random() < 0.5 ? COLOR.Gold : COLOR.White;
  }

  function makePistilColor(shellColor) {
    return (shellColor === COLOR.White || shellColor === COLOR.Gold) ? randomColor({ notColor: shellColor }) : whiteOrGold();
  }

const crysanthemumShell = (size=1) => {
	const glitter = Math.random() < 0.25;
	const singleColor = Math.random() < 0.72;
	const color = singleColor ? randomColor({ limitWhite: true }) : [randomColor(), randomColor({ notSame: true })];
	const pistil = singleColor && Math.random() < 0.42;
	const pistilColor = pistil && makePistilColor(color);
	const secondColor = singleColor && (Math.random() < 0.2 || color === COLOR.White) ? pistilColor || randomColor({ notColor: color, limitWhite: true }) : null;
	const streamers = !pistil && color !== COLOR.White && Math.random() < 0.42;
	let starDensity = glitter ? 1.1 : 1.25;
	if (isLowQuality) starDensity *= 0.8;
	if (isHighQuality) starDensity = 1.2;
	return {
		shellSize: size,
		spreadSize: 300 + size * 100,
		starLife: 900 + size * 200,
		starDensity,
		color,
		secondColor,
		glitter: glitter ? 'light' : '',
		glitterColor: whiteOrGold(),
		pistil,
		pistilColor,
		streamers
	};
};

const ghostShell = (size=1) => {
	const shell = crysanthemumShell(size);
	shell.starLife *= 1.5;
	let ghostColor = randomColor({ notColor: COLOR.White });
	shell.streamers = true;
	const pistil = Math.random() < 0.42;
	const pistilColor = pistil && makePistilColor(ghostColor);
	shell.color = INVISIBLE;
	shell.secondColor = ghostColor;
	shell.glitter = '';
	return shell;
};

const strobeShell = (size=1) => {
	const color = randomColor({ limitWhite: true });
	return {
		shellSize: size,
		spreadSize: 280 + size * 92,
		starLife: 1100 + size * 200,
		starLifeVariation: 0.40,
		starDensity: 1.1,
		color,
		glitter: 'light',
		glitterColor: COLOR.White,
		strobe: true,
		strobeColor: Math.random() < 0.5 ? COLOR.White : null,
		pistil: Math.random() < 0.5,
		pistilColor: makePistilColor(color)
	};
};

const palmShell = (size=1) => {
	const color = randomColor();
	const thick = Math.random() < 0.5;
	return {
		shellSize: size,
		color,
		spreadSize: 250 + size * 75,
		starDensity: thick ? 0.15 : 0.4,
		starLife: 1800 + size * 200,
		glitter: thick ? 'thick' : 'heavy'
	};
};

const ringShell = (size=1) => {
	const color = randomColor();
	const pistil = Math.random() < 0.75;
	return {
		shellSize: size,
		ring: true,
		color,
		spreadSize: 300 + size * 100,
		starLife: 900 + size * 200,
		starCount: 2.2 * PI_2 * (size+1),
		pistil,
		pistilColor: makePistilColor(color),
		glitter: !pistil ? 'light' : '',
		glitterColor: color === COLOR.Gold ? COLOR.Gold : COLOR.White,
		streamers: Math.random() < 0.3
	};
};

const crossetteShell = (size=1) => {
	const color = randomColor({ limitWhite: true });
	return {
		shellSize: size,
		spreadSize: 300 + size * 100,
		starLife: 750 + size * 160,
		starLifeVariation: 0.4,
		starDensity: 0.85,
		color,
		crossette: true,
		pistil: Math.random() < 0.5,
		pistilColor: makePistilColor(color)
	};
};

const floralShell = (size=1) => ({
	shellSize: size,
	spreadSize: 300 + size * 120,
	starDensity: 0.12,
	starLife: 500 + size * 50,
	starLifeVariation: 0.5,
	color: Math.random() < 0.65 ? 'random' : (Math.random() < 0.15 ? randomColor() : [randomColor(), randomColor({ notSame: true })]),
	floral: true
});

const fallingLeavesShell = (size=1) => ({
	shellSize: size,
	color: INVISIBLE,
	spreadSize: 300 + size * 120,
	starDensity: 0.12,
	starLife: 500 + size * 50,
	starLifeVariation: 0.5,
	glitter: 'medium',
	glitterColor: COLOR.Gold,
	fallingLeaves: true
});

const willowShell = (size=1) => ({
	shellSize: size,
	spreadSize: 300 + size * 100,
	starDensity: 0.6,
	starLife: 3000 + size * 300,
	glitter: 'willow',
	glitterColor: COLOR.Gold,
	color: INVISIBLE
});

const crackleShell = (size=1) => {
	const color = Math.random() < 0.75 ? COLOR.Gold : randomColor();
	return {
		shellSize: size,
		spreadSize: 380 + size * 75,
		starDensity: isLowQuality ? 0.65 : 1,
		starLife: 600 + size * 100,
		starLifeVariation: 0.32,
		glitter: 'light',
		glitterColor: COLOR.Gold,
		color,
		crackle: true,
		pistil: Math.random() < 0.65,
		pistilColor: makePistilColor(color)
	};
};

const horsetailShell = (size=1) => {
	const color = randomColor();
	return {
		shellSize: size,
		horsetail: true,
		color,
		spreadSize: 250 + size * 38,
		starDensity: 0.9,
		starLife: 2500 + size * 300,
		glitter: 'medium',
		glitterColor: Math.random() < 0.5 ? whiteOrGold() : color,
		strobe: color === COLOR.White
	};
};

  const shellTypes = {
    'Random': randomShell,
    'Crackle': crackleShell,
    'Crossette': crossetteShell,
    'Crysanthemum': crysanthemumShell,
    'Falling Leaves': fallingLeavesShell,
    'Floral': floralShell,
    'Ghost': ghostShell,
    'Horse Tail': horsetailShell,
    'Palm': palmShell,
    'Ring': ringShell,
    'Strobe': strobeShell,
    'Willow': willowShell
  };
  const shellNames = Object.keys(shellTypes);

  function randomShellName() {
    return Math.random() < 0.5 ? 'Crysanthemum' : shellNames[(Math.random() * (shellNames.length - 1) + 1) | 0 ];
  }

  function randomShell(size) {
    if (IS_HEADER) return randomFastShell()(size);
    return shellTypes[randomShellName()](size);
  }

  function shellFromConfig(size) {
    return shellTypes[shellNameSelector()](size);
  }
  
  const fastShellBlacklist = ['Falling Leaves', 'Floral', 'Willow'];
  function randomFastShell() {
    const isRandom = shellNameSelector() === 'Random';
    let shellName = isRandom ? randomShellName() : shellNameSelector();
    if (isRandom) {
      while (fastShellBlacklist.includes(shellName)) {
        shellName = randomShellName();
      }
    }
    return shellTypes[shellName];
  }

  class Stage {
    constructor(canvasIdOrEl, options) {
      const canvas = typeof canvasIdOrEl === 'string' ? document.getElementById(canvasIdOrEl) : canvasIdOrEl;
      if (!canvas || !(canvas instanceof HTMLCanvasElement)) {
        throw new Error('Canvas not found or invalid element for Stage.');
      }
      this.canvas = canvas;
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        throw new Error('Could not get 2D context from canvas.');
      }
      this.ctx = ctx;
      this.dpr = (typeof window !== 'undefined' && window.devicePixelRatio) || 1;
      this.width = canvas.width;
      this.height = canvas.height;
      this._listeners = {};

      if (options && options.disableHighDPI) {
        // High DPI handling
      }
      this.resize(this.width, this.height);
    }

    resize(width, height) {
      this.width = width;
      this.height = height;
      this.canvas.width = width * this.dpr;
      this.canvas.height = height * this.dpr;
      this.canvas.style.width = width + 'px';
      this.canvas.style.height = height + 'px';
    }

    addEventListener(event, handler) {
      if (!this._listeners[event]) {
        this._listeners[event] = [];
      }
      this._listeners[event].push(handler);
    }

    _tick(frameTime, lag) {
      if (this._listeners['ticker']) {
        this._listeners['ticker'].forEach(handler => handler(frameTime, lag));
      }
    }
  }

  const MyMath = {
    random: (min, max) => Math.random() * (max - min) + min,
    randomChoice: (arr) => arr[Math.random() * arr.length | 0],
    clamp: (value, min, max) => Math.min(Math.max(value, min), max),
    pointDist: (x1, y1, x2, y2) => {
      const dx = x2 - x1;
      const dy = y2 - y1;
      return Math.sqrt(dx*dx + dy*dy);
    },
    pointAngle: (x1, y1, x2, y2) => Math.atan2(y2 - y1, x2 - x1),
  };

  function internalInit() {
    togglePause(false);
    configDidUpdate();
    handleResize();
  }

  function fitShellPositionInBoundsH(position) {
    const edge = 0.18;
    return (1 - edge * 2) * position + edge;
  }

  function fitShellPositionInBoundsV(position) {
    return position * 0.75;
  }

  function getRandomShellPositionH() {
    return fitShellPositionInBoundsH(Math.random());
  }

  function getRandomShellPositionV() {
    return fitShellPositionInBoundsV(Math.random());
  }

  function getRandomShellSize() {
    const baseSize = shellSizeSelector();
    const maxVariance = Math.min(2.5, baseSize);
    const variance = Math.random() * maxVariance;
    const size = baseSize - variance;
    const height = maxVariance === 0 ? Math.random() : 1 - (variance / maxVariance);
    const centerOffset = Math.random() * (1 - height * 0.65) * 0.5;
    const x = Math.random() < 0.5 ? 0.5 - centerOffset : 0.5 + centerOffset;
    return {
      size,
      x: fitShellPositionInBoundsH(x),
      height: fitShellPositionInBoundsV(height)
    };
  }

  function launchShellFromConfig(event) {
    const shell = new Shell(shellFromConfig(shellSizeSelector()));
    if (!mainStage) return; // Guard
    const w = mainStage.width;
    const h = mainStage.height;
    shell.launch(
      event ? event.x / w : getRandomShellPositionH(),
      event ? 1 - event.y / h : getRandomShellPositionV()
    );
  }

function seqRandomShell() {
	const size = getRandomShellSize();
	const shell = new Shell(shellFromConfig(size.size));
	shell.launch(size.x, size.height);
	let extraDelay = shell.starLife;
	if (shell.fallingLeaves) {
		extraDelay = 4600;
	}
	return 900 + Math.random() * 600 + extraDelay;
}

function seqRandomFastShell() {
	const shellType = randomFastShell();
	const size = getRandomShellSize();
	const shell = new Shell(shellType(size.size));
	shell.launch(size.x, size.height);
	let extraDelay = shell.starLife;
	return 900 + Math.random() * 600 + extraDelay;
}

function seqTwoRandom() {
	const size1 = getRandomShellSize();
	const size2 = getRandomShellSize();
	const shell1 = new Shell(shellFromConfig(size1.size));
	const shell2 = new Shell(shellFromConfig(size2.size));
	const leftOffset = Math.random() * 0.2 - 0.1;
	const rightOffset = Math.random() * 0.2 - 0.1;
	shell1.launch(0.3 + leftOffset, size1.height);
	setTimeout(() => {
		shell2.launch(0.7 + rightOffset, size2.height);
	}, 100);
	let extraDelay = Math.max(shell1.starLife, shell2.starLife);
	if (shell1.fallingLeaves || shell2.fallingLeaves) {
		extraDelay = 4600;
	}
	return 900 + Math.random() * 600 + extraDelay;
}

function seqTriple() {
	const shellType = randomFastShell();
	const baseSize = shellSizeSelector();
	const smallSize = Math.max(0, baseSize - 1.25);
	const offset = Math.random() * 0.08 - 0.04;
	const shell1 = new Shell(shellType(baseSize));
	shell1.launch(0.5 + offset, 0.7);
	const leftDelay = 1000 + Math.random() * 400;
	const rightDelay = 1000 + Math.random() * 400;
	setTimeout(() => {
		const offset = Math.random() * 0.08 - 0.04;
		const shell2 = new Shell(shellType(smallSize));
		shell2.launch(0.2 + offset, 0.1);
	}, leftDelay);
	setTimeout(() => {
		const offset = Math.random() * 0.08 - 0.04;
		const shell3 = new Shell(shellType(smallSize));
		shell3.launch(0.8 + offset, 0.1);
	}, rightDelay);
	return 4000;
}

function seqPyramid() {
	const barrageCountHalf = IS_DESKTOP ? 7 : 4;
	const largeSize = shellSizeSelector();
	const smallSize = Math.max(0, largeSize - 3);
	const randomMainShell = Math.random() < 0.78 ? crysanthemumShell : ringShell;
	const randomSpecialShell = randomShell;

	function launchShell(x, useSpecial) {
		const isRandom = shellNameSelector() === 'Random';
		let shellType = isRandom
			? useSpecial ? randomSpecialShell : randomMainShell
			: shellTypes[shellNameSelector()];
		const shell = new Shell(shellType(useSpecial ? largeSize : smallSize));
		const height = x <= 0.5 ? x / 0.5 : (1 - x) / 0.5;
		shell.launch(x, useSpecial ? 0.75 : height * 0.42);
	}
	let count = 0;
	let delay = 0;
	while(count <= barrageCountHalf) {
		if (count === barrageCountHalf) {
			setTimeout(() => {
				launchShell(0.5, true);
			}, delay);
		} else {
			const offset = count / barrageCountHalf * 0.5;
			const delayOffset = Math.random() * 30 + 30;
			setTimeout(() => {
				launchShell(offset, false);
			}, delay);
			setTimeout(() => {
				launchShell(1 - offset, false);
			}, delay + delayOffset);
		}
		count++;
		delay += 200;
	}
	return 3400 + barrageCountHalf * 250;
}

function seqSmallBarrage() {
	seqSmallBarrage.lastCalled = Date.now();
	const barrageCount = IS_DESKTOP ? 11 : 5;
	const specialIndex = IS_DESKTOP ? 3 : 1;
	const shellSize = Math.max(0, shellSizeSelector() - 2);
	const randomMainShell = Math.random() < 0.78 ? crysanthemumShell : ringShell;
	const randomSpecialShell = randomFastShell();

	function launchShell(x, useSpecial) {
		const isRandom = shellNameSelector() === 'Random';
		let shellType = isRandom
			? useSpecial ? randomSpecialShell : randomMainShell
			: shellTypes[shellNameSelector()];
		const shell = new Shell(shellType(shellSize));
		const height = (Math.cos(x*5*Math.PI + PI_HALF) + 1) / 2;
		shell.launch(x, height * 0.75);
	}
	let count = 0;
	let delay = 0;
	while(count < barrageCount) {
		if (count === 0) {
			launchShell(0.5, false)
			count += 1;
		}
		else {
			const offset = (count + 1) / barrageCount / 2;
			const delayOffset = Math.random() * 30 + 30;
			const useSpecial = count === specialIndex;
			setTimeout(() => {
				launchShell(0.5 + offset, useSpecial);
			}, delay);
			setTimeout(() => {
				launchShell(0.5 - offset, useSpecial);
			}, delay + delayOffset);
			count += 2;
		}
		delay += 200;
	}
	return 3400 + barrageCount * 120;
}
seqSmallBarrage.cooldown = 15000;
seqSmallBarrage.lastCalled = Date.now();

  const sequences = [
    seqRandomShell,
    seqTwoRandom,
    seqTriple,
    seqPyramid,
    seqSmallBarrage
  ];

  let isFirstSeq = true;
  const finaleCount = 32;
  let currentFinaleCount = 0;
  function startSequence() {
    if (isFirstSeq) {
      isFirstSeq = false;
      if (IS_HEADER) {
        return seqTwoRandom();
      } else {
        const shell = new Shell(crysanthemumShell(shellSizeSelector()));
        shell.launch(0.5, 0.5);
        return 2400;
      }
    }

    if (finaleSelector()) {
      seqRandomFastShell()(); 
      if (currentFinaleCount < finaleCount) {
        currentFinaleCount++;
        return 170;
      } else {
        currentFinaleCount = 0;
        return 6000;
      }
    }

    const rand = Math.random();
    if (rand < 0.08 && Date.now() - seqSmallBarrage.lastCalled > seqSmallBarrage.cooldown) {
      return seqSmallBarrage();
    }
    if (rand < 0.1) {
      return seqPyramid();
    }
    if (rand < 0.6 && !IS_HEADER) {
      return seqRandomShell();
    } else if (rand < 0.8) {
      return seqTwoRandom();
    } else if (rand < 1) {
      return seqTriple();
    }
    return seqRandomShell();
  }

  function handleResize() {
    if (typeof window === 'undefined' || !stageContainerEl) {
      console.log('[Fireworks.svelte] handleResize: Aborted (window or stageContainerEl undefined)');
      return;
    }
    const w = stageContainerEl.clientWidth;
    const h = stageContainerEl.clientHeight;
    
    const containerW = Math.min(w, MAX_WIDTH);
    const containerH = Math.min(h, MAX_HEIGHT);
    
    console.log(`[Fireworks.svelte] handleResize: w=${w}, h=${h}, containerW=${containerW}, containerH=${containerH}`);

    if (stages && stages.length) {
        stages.forEach(stage => stage.resize(containerW, containerH));
    } else {
      console.log('[Fireworks.svelte] handleResize: Stages not yet initialized or empty.');
    }
    
    const scaleFactor = scaleFactorSelector();
    stageW = containerW / scaleFactor;
    stageH = containerH / scaleFactor;
    console.log(`[Fireworks.svelte] handleResize: scaleFactor=${scaleFactor}, stageW=${stageW}, stageH=${stageH}`);
  }
  
  let currentFrame = 0;
  let autoLaunchTime = 0;

  function updateGlobals(timeStep, lag) {
    currentFrame++;
    if (store.state.config.autoLaunch) {
      autoLaunchTime -= timeStep;
      if (autoLaunchTime <= 0) {
        autoLaunchTime = startSequence() * 1.25;
      }
    }
  }

  function update(frameTime, lag) {
    if (!isRunning()) return;
    const timeStep = frameTime * simSpeed;
    const speed = simSpeed * lag;
    updateGlobals(timeStep, lag);

    const starDrag = 1 - (1 - Star.airDrag) * speed;
    const starDragHeavy = 1 - (1 - Star.airDragHeavy) * speed;
    const sparkDrag = 1 - (1 - Spark.airDrag) * speed;
    const gAcc = timeStep / 1000 * GRAVITY;

    COLOR_CODES_W_INVIS.forEach(color => {
      const stars = Star.active[color];
      for (let i = stars.length - 1; i >= 0; i = i - 1) {
        const star = stars[i];
        if (star.updateFrame === currentFrame) continue;
        star.updateFrame = currentFrame;
        star.life -= timeStep;
        if (star.life <= 0) {
          stars.splice(i, 1);
          Star.returnInstance(star);
        } else {
          const burnRate = Math.pow(star.life / star.fullLife, 0.5);
          const burnRateInverse = 1 - burnRate;
          star.prevX = star.x;
          star.prevY = star.y;
          star.x += star.speedX * speed;
          star.y += star.speedY * speed;
          if (!star.heavy) {
            star.speedX *= starDrag;
            star.speedY *= starDrag;
          } else {
            star.speedX *= starDragHeavy;
            star.speedY *= starDragHeavy;
          }
          star.speedY += gAcc;
          if (star.spinRadius) {
            star.spinAngle += star.spinSpeed * speed;
            star.x += Math.sin(star.spinAngle) * star.spinRadius * speed;
            star.y += Math.cos(star.spinAngle) * star.spinRadius * speed;
          }
          if (star.sparkFreq) {
            star.sparkTimer -= timeStep;
            while (star.sparkTimer < 0) {
              star.sparkTimer += star.sparkFreq * 0.75 + star.sparkFreq * burnRateInverse * 4;
              Spark.add(
                star.x, star.y, star.sparkColor,
                Math.random() * PI_2,
                Math.random() * star.sparkSpeed * burnRate,
                star.sparkLife * 0.8 + Math.random() * star.sparkLifeVariation * star.sparkLife
              );
            }
          }
          if (star.life < star.transitionTime) {
            if (star.secondColor && !star.colorChanged) {
              star.colorChanged = true;
              star.color = star.secondColor;
              stars.splice(i, 1);
              Star.active[star.secondColor].push(star);
              if (star.secondColor === INVISIBLE) star.sparkFreq = 0;
            }
            if (star.strobe) {
              star.visible = Math.floor(star.life / star.strobeFreq) % 3 === 0;
            }
          }
        }
      }
      const sparks = Spark.active[color];
      for (let i = sparks.length - 1; i >= 0; i = i - 1) {
        const spark = sparks[i];
        spark.life -= timeStep;
        if (spark.life <= 0) {
          sparks.splice(i, 1);
          Spark.returnInstance(spark);
        } else {
          spark.prevX = spark.x;
          spark.prevY = spark.y;
          spark.x += spark.speedX * speed;
          spark.y += spark.speedY * speed;
          spark.speedX *= sparkDrag;
          spark.speedY *= sparkDrag;
          spark.speedY += gAcc;
        }
      }
    });
    renderFrame(speed);
  }

  function renderFrame(speed) {
    if (!trailsStage || !mainStage) return;

    // console.log(`[Fireworks.svelte] renderFrame called with speed: ${speed}`); // Add this log

    const { dpr } = mainStage;
    const width = stageW;
    const height = stageH;
    const trailsCtx = trailsStage.ctx;
    const mainCtx = mainStage.ctx;

    const scaleFactor = scaleFactorSelector();
    trailsCtx.save(); // Save context state
    mainCtx.save();   // Save context state
    trailsCtx.scale(dpr * scaleFactor, dpr * scaleFactor);
    mainCtx.scale(dpr * scaleFactor, dpr * scaleFactor);
    
    const fillStyleOpacity = store.state.config.longExposure ? 0.0025 : 0.175 * speed;
    // console.log(`[Fireworks.svelte] renderFrame: fillStyleOpacity = ${fillStyleOpacity}, speed = ${speed}, longExposure = ${store.state.config.longExposure}`);

    // Changed to 'destination-out' to only fade existing content (firework trails)
    // This will fade the existing trails but keep the background visible
    trailsCtx.globalCompositeOperation = 'destination-out';
    trailsCtx.fillStyle = `rgba(0, 0, 0, ${fillStyleOpacity})`;
    trailsCtx.fillRect(0, 0, width, height);
    
    // Reset to source-over for the new firework content
    trailsCtx.globalCompositeOperation = 'source-over';
    mainCtx.clearRect(0, 0, width, height);

    while (BurstFlash.active.length) {
      const bf = BurstFlash.active.pop();
      if (!bf) continue; // Guard if pop returns undefined
      const burstGradient = trailsCtx.createRadialGradient(bf.x, bf.y, 0, bf.x, bf.y, bf.radius);
      burstGradient.addColorStop(0.024, 'rgba(255, 255, 255, 1)');
      burstGradient.addColorStop(0.125, 'rgba(255, 160, 20, 0.2)');
      burstGradient.addColorStop(0.32, 'rgba(255, 140, 20, 0.11)');
      burstGradient.addColorStop(1, 'rgba(255, 120, 20, 0)');
      trailsCtx.fillStyle = burstGradient;
      trailsCtx.fillRect(bf.x - bf.radius, bf.y - bf.radius, bf.radius * 2, bf.radius * 2);
      BurstFlash.returnInstance(bf);
    }

    trailsCtx.globalCompositeOperation = 'lighten';
    trailsCtx.lineWidth = Star.drawWidth;
    trailsCtx.lineCap = isLowQuality ? 'square' : 'round';
    mainCtx.strokeStyle = '#fff';
    mainCtx.lineWidth = 1;
    mainCtx.beginPath();

    COLOR_CODES.forEach(color => {
      const stars = Star.active[color];
      trailsCtx.strokeStyle = color;
      trailsCtx.beginPath();
      stars.forEach(star => {
        if (star.visible) {
          trailsCtx.moveTo(star.x, star.y);
          trailsCtx.lineTo(star.prevX, star.prevY);
        }
      });
      trailsCtx.stroke();
    });
    mainCtx.stroke();

    trailsCtx.lineWidth = Spark.drawWidth;
    trailsCtx.lineCap = 'butt';
    COLOR_CODES.forEach(color => {
      const sparks = Spark.active[color];
      trailsCtx.strokeStyle = color;
      trailsCtx.beginPath();
      sparks.forEach(spark => {
        trailsCtx.moveTo(spark.x, spark.y);
        trailsCtx.lineTo(spark.prevX, spark.prevY);
      });
      trailsCtx.stroke();
    });

    trailsCtx.restore(); // Restore context state
    mainCtx.restore();   // Restore context state
  }
  
function createParticleArc(start, arcLength, count, randomness, particleFactory) {
	const angleDelta = arcLength / count;
	const end = start + arcLength - (angleDelta * 0.5);
	if (end > start) {
		for (let angle=start; angle<end; angle=angle+angleDelta) {
			particleFactory(angle + Math.random() * angleDelta * randomness);
		}
	} else {
		for (let angle=start; angle>end; angle=angle+angleDelta) {
			particleFactory(angle + Math.random() * angleDelta * randomness);
		}
	}
}

function createBurst(count, particleFactory, startAngle=0, arcLength=PI_2) {
	const R = 0.5 * Math.sqrt(count/Math.PI);
	const C = 2 * R * Math.PI;
	const C_HALF = C / 2;
	for (let i=0; i<=C_HALF; i++) {
		const ringAngle = i / C_HALF * PI_HALF;
		const ringSize = Math.cos(ringAngle);
		const partsPerFullRing = C * ringSize;
		const partsPerArc = partsPerFullRing * (arcLength / PI_2);
		const angleInc = PI_2 / partsPerFullRing;
		const angleOffset = Math.random() * angleInc + startAngle;
		const maxRandomAngleOffset = angleInc * 0.33;
		for (let i=0; i<partsPerArc; i++) {
			const randomAngleOffset = Math.random() * maxRandomAngleOffset;
			let angle = angleInc * i + angleOffset + randomAngleOffset;
			particleFactory(angle, ringSize);
		}
	}
}
function crossetteEffect(star) {
	const startAngle = Math.random() * PI_HALF;
	createParticleArc(startAngle, PI_2, 4, 0.5, (angle) => {
		Star.add(
			star.x, star.y, star.color, angle,
			Math.random() * 0.6 + 0.75,
			600
		);
	});
}

function floralEffect(star) {
	const count = 12 + 6 * quality;
	createBurst(count, (angle, speedMult) => {
		Star.add(
			star.x, star.y, star.color, angle,
			speedMult * 2.4,
			1000 + Math.random() * 300,
			star.speedX, star.speedY
		);
	});
	BurstFlash.add(star.x, star.y, 46);
}

function fallingLeavesEffect(star) {
	createBurst(7, (angle, speedMult) => {
		const newStar = Star.add(
			star.x, star.y, INVISIBLE, angle,
			speedMult * 2.4,
			2400 + Math.random() * 600,
			star.speedX, star.speedY
		);
		newStar.sparkColor = COLOR.Gold;
		newStar.sparkFreq = 144 / quality;
		newStar.sparkSpeed = 0.28;
		newStar.sparkLife = 750;
		newStar.sparkLifeVariation = 3.2;
	});
	BurstFlash.add(star.x, star.y, 46);
}

function crackleEffect(star) {
	const count = isHighQuality ? 32 : 16;
	createParticleArc(0, PI_2, count, 1.8, (angle) => {
		Spark.add(
			star.x, star.y, COLOR.Gold, angle,
			Math.pow(Math.random(), 0.45) * 2.4,
			300 + Math.random() * 200
		);
	});
}
  
class Shell {
	constructor(options) {
		Object.assign(this, options);
		this.starLifeVariation = options.starLifeVariation || 0.125;
		this.color = options.color || randomColor();
		this.glitterColor = options.glitterColor || this.color;
		if (!this.starCount) {
			const density = options.starDensity || 1;
			const scaledSize = this.spreadSize / 54;
			this.starCount = Math.max(6, scaledSize * scaledSize * density);
		}
	}

	launch(position, launchHeight) {
		const width = stageW;
		const height = stageH;
		const hpad = 60;
		const vpad = 50;
		const minHeightPercent = 0.45;
		const minHeight = height - height * minHeightPercent;
		const launchX = position * (width - hpad * 2) + hpad;
		const launchY = height;
		const burstY = minHeight - (launchHeight * (minHeight - vpad));
		const launchDistance = launchY - burstY;
		const launchVelocity = Math.pow(launchDistance * 0.04, 0.64);
		this.comet = Star.add(
			launchX, launchY,
			typeof this.color === 'string' && this.color !== 'random' ? this.color : COLOR.White,
			Math.PI,
			launchVelocity * (this.horsetail ? 1.2 : 1),
			launchVelocity * (this.horsetail ? 100 : 400)
		);
		this.comet.heavy = true;
		this.comet.spinRadius = MyMath.random(0.32, 0.85);
		this.comet.sparkFreq = 32 / quality;
		if (isHighQuality) this.comet.sparkFreq = 8;
		this.comet.sparkLife = 320;
		this.comet.sparkLifeVariation = 3;
		if (this.glitter === 'willow' || this.fallingLeaves) {
			this.comet.sparkFreq = 20 / quality;
			this.comet.sparkSpeed = 0.5;
			this.comet.sparkLife = 500;
		}
		if (this.color === INVISIBLE) {
			this.comet.sparkColor = COLOR.Gold;
		}
		if (Math.random() > 0.4 && !this.horsetail) {
			this.comet.secondColor = INVISIBLE;
			this.comet.transitionTime = Math.pow(Math.random(), 1.5) * 700 + 500;
		}
		this.comet.onDeath = (comet) => this.burst(comet.x, comet.y);
	}
	burst(x, y) {
		const speed = this.spreadSize / 96;
		let color, onDeath, sparkFreq, sparkSpeed, sparkLife;
		let sparkLifeVariation = 0.25;
		if (this.crossette) onDeath = (star) => {
			crossetteEffect(star);
		}
		if (this.crackle) onDeath = (star) => {
			crackleEffect(star);
		}
		if (this.floral) onDeath = floralEffect;
		if (this.fallingLeaves) onDeath = fallingLeavesEffect;
		if (this.glitter === 'light') { sparkFreq = 400; sparkSpeed = 0.3; sparkLife = 300; sparkLifeVariation = 2; }
		else if (this.glitter === 'medium') { sparkFreq = 200; sparkSpeed = 0.44; sparkLife = 700; sparkLifeVariation = 2; }
		else if (this.glitter === 'heavy') { sparkFreq = 80; sparkSpeed = 0.8; sparkLife = 1400; sparkLifeVariation = 2; }
		else if (this.glitter === 'thick') { sparkFreq = 16; sparkSpeed = isHighQuality ? 1.65 : 1.5; sparkLife = 1400; sparkLifeVariation = 3; }
		else if (this.glitter === 'streamer') { sparkFreq = 32; sparkSpeed = 1.05; sparkLife = 620; sparkLifeVariation = 2; }
		else if (this.glitter === 'willow') { sparkFreq = 120; sparkSpeed = 0.34; sparkLife = 1400; sparkLifeVariation = 3.8; }
		if (sparkFreq) sparkFreq = sparkFreq / quality; // Check if sparkFreq is defined before division
		const starFactory = (angle, speedMult) => {
			const standardInitialSpeed = this.spreadSize / 1800;
			const star = Star.add(
				x, y, color || randomColor(), angle, speedMult * speed,
				this.starLife + Math.random() * this.starLife * this.starLifeVariation,
				this.horsetail ? this.comet && this.comet.speedX : 0,
				this.horsetail ? this.comet && this.comet.speedY : -standardInitialSpeed
			);
			if (this.secondColor) {
				star.transitionTime = this.starLife * (Math.random() * 0.05 + 0.32);
				star.secondColor = this.secondColor;
			}
			if (this.strobe) {
				star.transitionTime = this.starLife * (Math.random() * 0.08 + 0.46);
				star.strobe = true;
				star.strobeFreq = Math.random() * 20 + 40;
				if (this.strobeColor) star.secondColor = this.strobeColor;
			}
			star.onDeath = onDeath;
			if (this.glitter) {
				star.sparkFreq = sparkFreq;
				star.sparkSpeed = sparkSpeed;
				star.sparkLife = sparkLife;
				star.sparkLifeVariation = sparkLifeVariation;
				star.sparkColor = this.glitterColor;
				star.sparkTimer = Math.random() * star.sparkFreq;
			}
		};
		if (typeof this.color === 'string') {
			if (this.color === 'random') color = null;
			else color = this.color;
			if (this.ring) {
				const ringStartAngle = Math.random() * Math.PI;
				const ringSquash = Math.pow(Math.random(), 2) * 0.85 + 0.15;
				createParticleArc(0, PI_2, this.starCount, 0, (angle) => {
					const initSpeedX = Math.sin(angle) * speed * ringSquash;
					const initSpeedY = Math.cos(angle) * speed;
					const newSpeed = MyMath.pointDist(0, 0, initSpeedX, initSpeedY);
					const newAngle = MyMath.pointAngle(0, 0, initSpeedX, initSpeedY) + ringStartAngle;
					const star = Star.add(
						x, y, color, newAngle, newSpeed,
						this.starLife + Math.random() * this.starLife * this.starLifeVariation
					);
					if (this.glitter) { 
                        star.sparkFreq = sparkFreq;
                        star.sparkSpeed = sparkSpeed;
                        star.sparkLife = sparkLife;
                        star.sparkLifeVariation = sparkLifeVariation;
                        star.sparkColor = this.glitterColor;
                        star.sparkTimer = Math.random() * star.sparkFreq;
                    }
				});
			} else {
				createBurst(this.starCount, starFactory);
			}
		} else if (Array.isArray(this.color)) {
            if (Math.random() < 0.5) {
				const start = Math.random() * Math.PI;
				const start2 = start + Math.PI;
				const arc = Math.PI;
				color = this.color[0];
				createBurst(this.starCount, starFactory, start, arc);
				color = this.color[1];
				createBurst(this.starCount, starFactory, start2, arc);
			} else {
				color = this.color[0];
				createBurst(this.starCount / 2, starFactory);
				color = this.color[1];
				createBurst(this.starCount / 2, starFactory);
			}
		} else {
			throw new Error('Invalid shell color.');
		}
		if (this.pistil) {
			const innerShell = new Shell({
				spreadSize: this.spreadSize * 0.5, starLife: this.starLife * 0.6,
				starLifeVariation: this.starLifeVariation, starDensity: 1.4,
				color: this.pistilColor, glitter: 'light',
				glitterColor: this.pistilColor === COLOR.Gold ? COLOR.Gold : COLOR.White
			});
			innerShell.burst(x, y);
		}
		if (this.streamers) {
			const innerShell = new Shell({
				spreadSize: this.spreadSize * 0.9, starLife: this.starLife * 0.8,
				starLifeVariation: this.starLifeVariation,
				starCount: Math.floor(Math.max(6, this.spreadSize / 45)),
				color: COLOR.White, glitter: 'streamer'
			});
			innerShell.burst(x, y);
		}
		BurstFlash.add(x, y, this.spreadSize / 4);
	}
}

  const BurstFlash = { 
    active: [], _pool: [],
    _new() { return {} },
    add(x, y, radius) {
      const instance = this._pool.pop() || this._new();
      instance.x = x; instance.y = y; instance.radius = radius;
      this.active.push(instance);
      return instance;
    },
    returnInstance(instance) { this._pool.push(instance); }
  };

  function createParticleCollection() {
    const collection = {};
    COLOR_CODES_W_INVIS.forEach(color => { collection[color] = []; });
    return collection;
  }

  const Star = {
    drawWidth: 3, airDrag: 0.98, airDragHeavy: 0.992,
    active: createParticleCollection(), _pool: [],
    _new() { return {} },
    add(x, y, color, angle, speed, life, speedOffX, speedOffY) {
      const instance = this._pool.pop() || this._new();
      Object.assign(instance, {
        visible: true, heavy: false, x, y, prevX: x, prevY: y, color,
        speedX: Math.sin(angle) * speed + (speedOffX || 0),
        speedY: Math.cos(angle) * speed + (speedOffY || 0),
        life, fullLife: life, spinAngle: Math.random() * PI_2, spinSpeed: 0.8,
        spinRadius: 0, sparkFreq: 0, sparkSpeed: 1, sparkTimer: 0, sparkColor: color,
        sparkLife: 750, sparkLifeVariation: 0.25, strobe: false,
      });
      this.active[color].push(instance);
      return instance;
    },
    returnInstance(instance) {
      instance.onDeath && instance.onDeath(instance);
      instance.onDeath = null; instance.secondColor = null; instance.transitionTime = 0; instance.colorChanged = false;
      this._pool.push(instance);
    }
  };

  const Spark = {
    drawWidth: 0, airDrag: 0.9,
    active: createParticleCollection(), _pool: [],
    _new() { return {} },
    add(x, y, color, angle, speed, life) {
      const instance = this._pool.pop() || this._new();
      Object.assign(instance, {
        x, y, prevX: x, prevY: y, color,
        speedX: Math.sin(angle) * speed,
        speedY: Math.cos(angle) * speed,
        life
      });
      this.active[color].push(instance);
      return instance;
    },
    returnInstance(instance) { this._pool.push(instance); }
  };

  let animationFrameId;

  onMount(() => {
    if (typeof window !== 'undefined') {
      trailsStage = new Stage(trailsCanvasEl);
      mainStage = new Stage(mainCanvasEl);
      stages = [trailsStage, mainStage];
      mainStage.addEventListener('ticker', update); // Register the update function
      console.log('[Fireworks.svelte] Stages initialized and ticker listener added:', stages);
      
      let lastTime = 0;
      function animationLoop(currentTime) {
        if (!lastTime) {
            lastTime = currentTime;
            console.log('[Fireworks.svelte] Animation loop started at', currentTime);
        }
        const deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        
        if (mainStage && typeof mainStage._tick === 'function') {
             mainStage._tick(deltaTime, deltaTime / (1000 / 60));
        } else {
            // console.warn('[Fireworks.svelte] mainStage._tick not found, calling update directly.');
            update(deltaTime, deltaTime / (1000/60));
        }
        animationFrameId = requestAnimationFrame(animationLoop);
      }
      animationFrameId = requestAnimationFrame(animationLoop);
      console.log('[Fireworks.svelte] Animation loop requested.');

      internalInit();
      console.log('[Fireworks.svelte] internalInit() called.');
      window.addEventListener('resize', handleResize);
    } else {
      console.log('[Fireworks.svelte] onMount: window is undefined (SSR?)');
    }
  });

  onDestroy(() => {
    console.log('[Fireworks.svelte] onDestroy triggered');
    if (typeof window !== 'undefined') {
      window.removeEventListener('resize', handleResize);
      if (animationFrameId) {
        cancelAnimationFrame(animationFrameId);
      }
      stages = [];
      trailsStage = null;
      mainStage = null;
      Star.active = createParticleCollection(); Star._pool = [];
      Spark.active = createParticleCollection(); Spark._pool = [];
      BurstFlash.active = []; BurstFlash._pool = [];
      console.log('[Fireworks.svelte] Cleaned up resources.');
    }
  });

</script>

<div class="fireworks-wrapper" bind:this={stageContainerEl}>
  <div class="stage-container">
    <canvas id="trails-canvas" bind:this={trailsCanvasEl}></canvas>
    <canvas id="main-canvas" bind:this={mainCanvasEl}></canvas>
  </div>
</div>

<style lang="scss">
  .fireworks-wrapper {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: transparent !important;
    overflow: hidden;
    z-index: 3; /* As per plan for layering within BackgroundLayout */
  }
  
  .stage-container { /* If .stage-container is a separate element inside .fireworks-wrapper */
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: transparent !important; /* Ensure this too if it exists */
    overflow: hidden; /* If necessary */
  }

  :global(#trails-canvas), :global(#main-canvas) {
    background-color: transparent !important;
    position: absolute;
    mix-blend-mode: lighten;
  }
</style>