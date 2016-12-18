import {ConfigurationService, IConfiguration} from '../../services/configuration';
import {LinkService, ILink} from '../../services/link';

export enum ElementType {
  LINK, CONFIG, ADDLINK
}

export interface ILinkElement {
  x: number;
  y: number;
  img: string;
  name: string;
  type: ElementType;

  destinationX: number;
  destinationY: number;
}

const totalTime: number = 0.3 * 1000; //2secs

export class MainController {
  private image: HTMLImageElement = null;
  public links: ILinkElement[] = [];
  private startTime: number = null;

  private isLoaded: boolean = false;

  private nbSquaresX = 5;
  private nbSquaresY = 5;
  private configuration: IConfiguration = null;
  public modeConfig: boolean = false;

  private more_x_x: number = null;
  private more_x_y: number = null;

  private less_x_x: number = null;
  private less_x_y: number = null;

  private more_y_x: number = null;
  private more_y_y: number = null;

  private less_y_x: number = null;
  private less_y_y: number = null;

  /* @ngInject */
  constructor(
    private Configuration: ConfigurationService,
    private Link: LinkService,
    private $timeout: ng.ITimeoutService,
    private $window: ng.IWindowService,
    private $q: ng.IQService,
    private $location: ng.ILocationService,
    private $scope: ng.IScope
  ) {
    this.load();
    angular.element(this.$window).bind('resize', () => {
      this.calculateProportions();
    });
  }

  public goto(pLink: ILinkElement) {
    if (pLink.name !== null) {
      this.Link.start({
          name: pLink.name,
          description: null,
          command: null,
          parameters: null,

          squareX: null,
          squareY: null
      });
    } else {
      switch (pLink.type) {
        case ElementType.CONFIG :
          this.modeConfig = !this.modeConfig;
          this.calculateProportions();
          //this.$location.url(pLink.link);
          break;
      }
    }
  }

  public moreX(): void {
    this.nbSquaresX++;
    this.calculateProportions();
  }

  public lessX(): void {
    if (this.nbSquaresX <= 1) {
      return;
    }
    this.nbSquaresX--;
    this.calculateProportions();
  }

  public moreY(): void {
    this.nbSquaresY++;
    this.calculateProportions();
  }

  public lessY(): void {
    if (this.nbSquaresY <= 1) {
      return;
    }
    this.nbSquaresY--;
    this.calculateProportions();
  }

  private load(): void {
    var lPromises: ng.IPromise<any>[] = [];
    lPromises.push(
      this.Configuration.getConfiguration().then((pConfiguration: IConfiguration) => {
        this.configuration = pConfiguration;

        this.nbSquaresX = this.configuration.nbSquaresX;
        this.nbSquaresY = this.configuration.nbSquaresY;
      })
    );
    lPromises.push(
      this.Link.getAllLinks().then((pLinks: ILink[]) => {
        var lLinks: ILinkElement[] = [];
        pLinks.forEach((pLink: ILink) => {
          lLinks.push({
              x: null,
              y: null,
              img: "image.png",

              destinationX: pLink.squareX,
              destinationY: pLink.squareY,
              name: pLink.name,
              type: ElementType.LINK
            });
        });

        //Add configuration link
        lLinks.push({
              x: null,
              y: null,
              img: "images/config.png",

              destinationX: -1,
              destinationY: -1,
              name: null,
              type: ElementType.CONFIG
        });
        this.links = lLinks;
      })
    );
    this.$q.all(lPromises).then(
      () => {
        this.isLoaded = true;
        this.init();
        angular.element(this.$window).bind('resize', () => {
          this.calculateProportions();
        });
        console.log(this);
      }, (pError) => {
        console.error(pError);
      }
    );
  }

  public sizeSquareX: number = 0;
  public sizeSquareY: number = 0;
  private centerX: number = 0;
  private centerY: number = 0;

  private calculateProportions(): void {
    var lSize: ClientRect = document.body.getBoundingClientRect();
    var lScreenWidth = lSize.width;
    var lScreenHeight = lSize.height;

    this.more_x_x = lScreenWidth / 2 - 40;
    this.less_x_x = lScreenWidth / 2 + 8;
    this.more_y_y = lScreenHeight / 2 - 40;
    this.less_y_y = lScreenHeight / 2 + 8;

    this.more_x_y = 0;
    this.less_x_y = 0;
    this.more_y_x = 0;
    this.less_y_x = 0;

    if (this.modeConfig) {
      lScreenWidth -= 30;
      lScreenHeight -= 30;
    }

    this.sizeSquareX = lScreenWidth / this.nbSquaresX;
    this.sizeSquareY = lScreenHeight / this.nbSquaresY;

    this.centerX = lScreenWidth / 2;
    this.centerY = lScreenHeight / 2;

    if (this.modeConfig) {
      //Check squares not occuped to add "ADD" icon on them
      var lSquares: boolean[][] = [];
      for (var i = 0; i < this.nbSquaresX; i++) {
        lSquares[i] = [];
        for (var j = 0; j < this.nbSquaresY; j++) {
          lSquares[i][j] = false;
        }
      }
      var lLinks: ILinkElement[] = [];
      this.links.forEach((pLink: ILinkElement) => {
        var lDestinationX: number = pLink.destinationX;
        var lDestinationY: number = pLink.destinationY;
        if (lDestinationX < 0) {
          //Icon config, go to last square
          lDestinationX = this.nbSquaresX - 1;
          lDestinationY = this.nbSquaresY - 1;
        }
        if (lDestinationX >= 0 && lDestinationX < this.nbSquaresX && lDestinationY >= 0 && lDestinationY < this.nbSquaresY) {
          if (lDestinationX === (this.nbSquaresX - 1) && lDestinationY === (this.nbSquaresY - 1) && pLink.type !== ElementType.CONFIG) {
            return;
          }
          lSquares[lDestinationX][lDestinationY] = true;
          lLinks.push(pLink);
        }
      });
      this.links = lLinks;
      for (var i = 0; i < this.nbSquaresX; i++) {
        for (var j = 0; j < this.nbSquaresY; j++) {
          if (lSquares[i][j]) {
            continue;
          }
          this.links.push({
            x: null,
            y: null,
            img: 'images/add.png',
            name: null,
            type: ElementType.ADDLINK,
            destinationX: i,
            destinationY: j
          });
        }
      }
      this.centerX += 30;
      this.centerY += 30;
    } else {
      var lLinks: ILinkElement[] = [];
      this.links.forEach((pLink: ILinkElement) => {
        if (pLink.type !== ElementType.ADDLINK) {
          lLinks.push(pLink);
        }
      });
      this.links = lLinks;
      this.centerX -= 30;
      this.centerY -= 30;
    }

    this.$timeout(this.start, 0);
  }

  private init(): void {
    this.startTime = null;
    this.calculateProportions();
  }

  private start = () => {
    if (this.startTime === null) {
      this.startTime = Date.now();
    }
    var lNow: number = Date.now();
    var lDiff: number = lNow - this.startTime;
    var lPercent: number = Math.min(1, lDiff / totalTime);

    this.links.forEach((pLink: ILinkElement) => {
      var lDestinationX: number = pLink.destinationX * this.sizeSquareX;
      var lDestinationY: number = pLink.destinationY * this.sizeSquareY;
      if (lDestinationX < 0) {
        //Icon config, go to last square
        lDestinationX = (this.nbSquaresX - 1) * this.sizeSquareX;
        lDestinationY = (this.nbSquaresY - 1) * this.sizeSquareY;
      }
      if (this.modeConfig) {
        lDestinationX += 30;
        lDestinationY += 30;
      }
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
