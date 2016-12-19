import {ConfigurationService, IConfiguration} from '../../services/configuration';
import {LinkService, ILink} from '../../services/link';

export enum ElementType {
  LINK, CONFIG
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
    var lHandler = () => {
      this.calculateProportions();
    };
    this.$scope.$on('$destroy', () => {
      angular.element(this.$window).unbind('resize', lHandler);
    });
    angular.element(this.$window).bind('resize', lHandler);
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
          this.$location.url("/configuration");
          break;
      }
    }
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

    this.centerX = lScreenWidth / 2;
    this.centerY = lScreenHeight / 2;

    this.sizeSquareX = lScreenWidth / this.configuration.nbSquaresX;
    this.sizeSquareY = lScreenHeight / this.configuration.nbSquaresY;

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
