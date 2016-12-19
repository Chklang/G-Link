import {ConfigurationService, IConfiguration} from '../../services/configuration';
import {LinkService, ILink} from '../../services/link';
import {AddLinkController} from './add-link/add-link.controller';

export enum ElementType {
  LINK, CONFIG, ADDLINK
}

export interface ILinkElement {
  x: number;
  y: number;
  img: string;
  name: string;
  description: string;
  command: string;
  parameters: string;
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

  public configuration: IConfiguration = null;

  public modeConfig: boolean = false;

  private more_x_x: number = null;
  private more_x_y: number = null;

  private less_x_x: number = null;
  private less_x_y: number = null;

  private more_y_x: number = null;
  private more_y_y: number = null;

  private less_y_x: number = null;
  private less_y_y: number = null;

  private save_x: number = null;
  private save_y: number = null;

  private cancel_x: number = null;
  private cancel_y: number = null;

  /* @ngInject */
  constructor(
    private Configuration: ConfigurationService,
    private Link: LinkService,
    private $timeout: ng.ITimeoutService,
    private $window: ng.IWindowService,
    private $q: ng.IQService,
    private $location: ng.ILocationService,
    private $scope: ng.IScope,
    private $uibModal: ng.ui.bootstrap.IModalService
  ) {
    this.load();
    angular.element(this.$window).bind('resize', () => {
      this.calculateProportions();
    });
  }

  public addLink(pLinkElement: ILinkElement) {
      var options: ng.ui.bootstrap.IModalSettings = {
        template: require('./add-link/add-link.html'),
        controller: AddLinkController,
        controllerAs: 'ctrl',
        resolve: {
        }
      };
      this.$uibModal.open(options).result.then((pLink: ILink) => {
        var lLinkElement: ILinkElement = {
            x: null,
            y: null,
            img: "image.png",

            destinationX: pLinkElement.destinationX,
            destinationY: pLinkElement.destinationY,
            name: pLink.name,
            description: pLink.description,
            command: pLink.command,
            parameters: pLink.parameters,
            type: ElementType.LINK
        };
        lLinkElement.x = lLinkElement.destinationX * this.sizeSquareX;
        lLinkElement.y = lLinkElement.destinationY * this.sizeSquareY;
        var lLinks: ILinkElement[] = [];
        this.links.forEach((pLinkActual: ILinkElement) => {
          if (pLinkActual.destinationX === pLinkElement.destinationX && pLinkActual.destinationY === pLinkElement.destinationY) {
            return;
          }
          lLinks.push(pLinkActual);
        });
        lLinks.push(lLinkElement);
        this.links = lLinks;
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
          if (this.modeConfig) {
            this.cancel();
          } else {
            this.modeConfig = !this.modeConfig;
            this.calculateProportions();
            //this.$location.url(pLink.link);
          }
          break;
        case ElementType.ADDLINK :
          this.addLink(pLink);
          break;
      }
    }
  }

  public moreX(): void {
    this.configuration.nbSquaresX++;
    this.calculateProportions();
  }

  public lessX(): void {
    if (this.configuration.nbSquaresX <= 1) {
      return;
    }
    this.configuration.nbSquaresX--;
    this.calculateProportions();
  }

  public moreY(): void {
    this.configuration.nbSquaresY++;
    this.calculateProportions();
  }

  public lessY(): void {
    if (this.configuration.nbSquaresY <= 1) {
      return;
    }
    this.configuration.nbSquaresY--;
    this.calculateProportions();
  }

  public save(): void {
    var lPromises: ng.IPromise<void>[] = [];
    lPromises.push(
      this.Configuration.saveConfiguration(this.configuration)
    );
    var lLinksToSave: ILink[] = [];
    this.links.forEach((pLink: ILinkElement) => {
      if (pLink.type !== ElementType.LINK) {
        return;
      }
      var lLink: ILink = {
        name: pLink.name,
        description: pLink.description,
        command: pLink.command,
        parameters: pLink.parameters,
        squareX: pLink.destinationX,
        squareY: pLink.destinationY
      };
      lLinksToSave.push(lLink);
    });
    lPromises.push(
      this.Link.setLinks(lLinksToSave)
    );
    this.$q.all(lPromises).then(() => {
      this.modeConfig = false;
      this.calculateProportions();
    });
  }

  public cancel(): void {
    this.modeConfig = false;
    this.isLoaded = false;
    this.load();
  }

  private load(): void {
    var lPromises: ng.IPromise<any>[] = [];
    lPromises.push(
      this.Configuration.getConfiguration().then((pConfiguration: IConfiguration) => {
        this.configuration = pConfiguration;
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
              description: pLink.description,
              command: pLink.command,
              parameters: pLink.parameters,
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
              description: null,
              command: null,
              parameters: null,
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
    this.save_x = lScreenWidth / 2 - 40;
    this.save_y = lScreenHeight - 40;
    this.cancel_x = lScreenWidth / 2 + 8;
    this.cancel_y = lScreenHeight - 40;

    this.more_x_y = 0;
    this.less_x_y = 0;
    this.more_y_x = 0;
    this.less_y_x = 0;

    this.centerX = lScreenWidth / 2;
    this.centerY = lScreenHeight / 2;

    if (this.modeConfig) {
      lScreenWidth -= 80;
      lScreenHeight -= 80;
    }

    this.sizeSquareX = lScreenWidth / this.configuration.nbSquaresX;
    this.sizeSquareY = lScreenHeight / this.configuration.nbSquaresY;

    if (this.modeConfig) {
      //Check squares not occuped to add "ADD" icon on them
      var lSquares: boolean[][] = [];
      for (var i = 0; i < this.configuration.nbSquaresX; i++) {
        lSquares[i] = [];
        for (var j = 0; j < this.configuration.nbSquaresY; j++) {
          lSquares[i][j] = false;
        }
      }
      var lLinks: ILinkElement[] = [];
      this.links.forEach((pLink: ILinkElement) => {
        var lDestinationX: number = pLink.destinationX;
        var lDestinationY: number = pLink.destinationY;
        if (lDestinationX < 0) {
          //Icon config, go to last square
          lDestinationX = this.configuration.nbSquaresX - 1;
          lDestinationY = this.configuration.nbSquaresY - 1;
        }
        if (lDestinationX >= 0 && lDestinationX < this.configuration.nbSquaresX &&
          lDestinationY >= 0 && lDestinationY < this.configuration.nbSquaresY) {
          if (lDestinationX === (this.configuration.nbSquaresX - 1) && lDestinationY === (this.configuration.nbSquaresY - 1) && pLink.type !== ElementType.CONFIG) {
            return;
          }
          lSquares[lDestinationX][lDestinationY] = true;
          lLinks.push(pLink);
        }
      });
      this.links = lLinks;
      for (var i = 0; i < this.configuration.nbSquaresX; i++) {
        for (var j = 0; j < this.configuration.nbSquaresY; j++) {
          if (lSquares[i][j]) {
            continue;
          }
          this.links.push({
            x: null,
            y: null,
            img: 'images/add.png',
            name: null,
            description: null,
            command: null,
            parameters: null,
            type: ElementType.ADDLINK,
            destinationX: i,
            destinationY: j
          });
        }
      }
    } else {
      var lLinks: ILinkElement[] = [];
      this.links.forEach((pLink: ILinkElement) => {
        if (pLink.type !== ElementType.ADDLINK) {
          lLinks.push(pLink);
        }
      });
      this.links = lLinks;
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

    //Calculate margins
    var lMarginLeft: number = this.centerX - (this.configuration.nbSquaresX * this.sizeSquareX) / 2;
    var lMarginTop: number = this.centerY - (this.configuration.nbSquaresY * this.sizeSquareY) / 2;

    this.links.forEach((pLink: ILinkElement) => {
      var lDestinationX: number = pLink.destinationX;
      var lDestinationY: number = pLink.destinationY;

      if (lDestinationX < 0) {
        //Icon config, go to last square
        lDestinationX = this.configuration.nbSquaresX - 1;
        lDestinationY = this.configuration.nbSquaresY - 1;
      }
      lDestinationX *= this.sizeSquareX;
      lDestinationY *= this.sizeSquareY;
      lDestinationX += lMarginLeft;
      lDestinationY += lMarginTop;

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
