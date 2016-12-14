export interface ILink {
  x: number;
  y: number;
  img: string;

  destinationX: number;
  destinationY: number;
}

const totalTime: number = 0.3 * 1000; //2secs

export class MainController {
  private image: HTMLImageElement = null;
  public links: ILink[] = [];
  private startTime: number = null;

  private nbX = 5;
  private nbY = 5;

  /* @ngInject */
  constructor(
    private $timeout: ng.ITimeoutService,
    private $window: ng.IWindowService
  ) {
    this.init();
    angular.element(this.$window).bind('resize', () => {
      this.calculateProportions();
    });
  }

  public sizeSquareX: number = 0;
  public sizeSquareY: number = 0;
  private centerX: number = 0;
  private centerY: number = 0;

  private calculateProportions(): void {
    var lSize: ClientRect = document.body.getBoundingClientRect();
    var lScreenWidth = lSize.width;
    var lScreenHeight = lSize.height;

    this.sizeSquareX = lScreenWidth / this.nbX;
    this.sizeSquareY = lScreenHeight / this.nbY;

    this.centerX = lScreenWidth / 2;
    this.centerY = lScreenHeight / 2;

    this.$timeout(this.start, 0);
  }

  private init(): void {
    this.links = [];
    this.startTime = null;
    for (var x = 0; x < 5; x++) {
      for (var y = 0; y < 5; y++) {
        this.links.push({
          x: null,
          y: null,
          img: "image.png",

          destinationX: x,
          destinationY: y
        });
      }
    }
    this.calculateProportions();
  }

  private start = () => {
    if (this.startTime === null) {
      this.startTime = Date.now();
    }
    var lNow: number = Date.now();
    var lDiff: number = lNow - this.startTime;
    var lPercent: number = Math.min(1, lDiff / totalTime);

    this.links.forEach((pLink: ILink) => {
      var lDestinationX: number = pLink.destinationX * this.sizeSquareX;
      var lDestinationY: number = pLink.destinationY * this.sizeSquareY;
      var lDistanceX = lDestinationX - this.centerX;
      var lPositionX = (lDistanceX * lPercent) + this.centerX;
      var lDistanceY = lDestinationY - this.centerY;
      var lPositionY = (lDistanceY * lPercent) + this.centerY;
      pLink.x = Math.round(lPositionX);
      pLink.y = Math.round(lPositionY);
    });
    if (lPercent < 1) {
      this.$timeout(this.start, 20);
    }
  }
}
