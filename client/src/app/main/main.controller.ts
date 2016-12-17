import {ConfigurationService, IConfiguration} from '../../services/configuration';
import {LinkService, ILink} from '../../services/link';

export interface ILinkElement {
  x: number;
  y: number;
  img: string;
  name: string;
  link: string;

  destinationX: number;
  destinationY: number;
}

const totalTime: number = 0.3 * 1000; //2secs

export class MainController {
  private image: HTMLImageElement = null;
  public links: ILinkElement[] = [];
  private startTime: number = null;

  private isLoaded: boolean = false;

  private nbX = 5;
  private nbY = 5;
  private configuration: IConfiguration = null;

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
      this.$location.url(pLink.link);
    }
  }

  private load(): void {
    var lPromises: ng.IPromise<any>[] = [];
    lPromises.push(
      this.Configuration.getConfiguration().then((pConfiguration: IConfiguration) => {
        this.configuration = pConfiguration;

        this.nbX = this.configuration.nbSquaresX;
        this.nbY = this.configuration.nbSquaresY;
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
              link: null
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
              link: '/configuration'
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

    this.sizeSquareX = lScreenWidth / this.nbX;
    this.sizeSquareY = lScreenHeight / this.nbY;

    this.centerX = lScreenWidth / 2;
    this.centerY = lScreenHeight / 2;

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
        lDestinationX = (this.nbX - 1) * this.sizeSquareX;
        lDestinationY = (this.nbY - 1) * this.sizeSquareY;
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
