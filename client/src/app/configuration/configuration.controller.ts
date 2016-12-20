import {ConfigurationService, IConfiguration} from '../../services/configuration';
import {LinkService, ILink} from '../../services/link';
import {AddLinkController} from './add-link/add-link.controller';
import {EditLinkController, IEditLinkContent} from './edit-link/edit-link.controller';
import {ConfirmController, IConfirmContent} from './confirm/confirm.controller';

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

  canRemove: boolean;
  canEdit: boolean;
}

const totalTime: number = 0.3 * 1000; //2secs

export class ConfigurationController {
  private image: HTMLImageElement = null;
  public links: ILinkElement[] = [];

  private isLoaded: boolean = false;

  public configuration: IConfiguration = null;

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
            type: ElementType.LINK,
            canEdit: true,
            canRemove: true
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

  public editLink(pLinkElement: ILinkElement) {
      var options: ng.ui.bootstrap.IModalSettings = {
        template: require('./edit-link/edit-link.html'),
        controller: EditLinkController,
        controllerAs: 'ctrl',
        resolve: {
          content: () : IEditLinkContent => {
            return {
              name: pLinkElement.name,
              description: pLinkElement.description,
              command: pLinkElement.command,
              parameters: pLinkElement.parameters
            }
          }
        }
      };
      this.$uibModal.open(options).result.then((pLink: ILink) => {
        pLinkElement.name = pLink.name;
        pLinkElement.description = pLink.description;
        pLinkElement.command = pLink.command;
        pLinkElement.parameters = pLink.parameters;
      });
  }

  public removeLink(pLinkElement: ILinkElement): void {
      var options: ng.ui.bootstrap.IModalSettings = {
        template: require('./confirm/confirm.html'),
        controller: ConfirmController,
        controllerAs: 'ctrl',
        resolve: {
          content: () : IConfirmContent => {
            return {
              title: 'Remove link',
              message: 'Are-you sure to want to remove this link?'
            }
          }
        }
      };
      this.$uibModal.open(options).result.then((pIsOk: boolean) => {
        if (pIsOk) {
          //Remove the link
          var lLinks: ILinkElement[] = [];
          this.links.forEach((pLinkActual: ILinkElement) => {
            if (pLinkActual.destinationX === pLinkElement.destinationX && pLinkActual.destinationY === pLinkElement.destinationY) {
              return;
            }
            lLinks.push(pLinkActual);
          });
          this.links = lLinks;
          this.calculateButtonsAdd();
          this.calculateProportions();
        }
      });
  }

  public goto(pLink: ILinkElement) {
    if (pLink.name === null) {
      switch (pLink.type) {
        case ElementType.CONFIG :
          this.cancel();
          break;
        case ElementType.ADDLINK :
          this.addLink(pLink);
          break;
      }
    }
  }

  public moreX(): void {
    this.configuration.nbSquaresX++;
    this.calculateButtonsAdd();
    this.calculateProportions();
  }

  public lessX(): void {
    if (this.configuration.nbSquaresX <= 1) {
      return;
    }
    this.configuration.nbSquaresX--;
    this.calculateButtonsAdd();
    this.calculateProportions();
  }

  public moreY(): void {
    this.configuration.nbSquaresY++;
    this.calculateButtonsAdd();
    this.calculateProportions();
  }

  public lessY(): void {
    if (this.configuration.nbSquaresY <= 1) {
      return;
    }
    this.configuration.nbSquaresY--;
    this.calculateButtonsAdd();
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
      this.$location.url("/");
    });
  }

  public cancel(): void {
      var options: ng.ui.bootstrap.IModalSettings = {
        template: require('./confirm/confirm.html'),
        controller: ConfirmController,
        controllerAs: 'ctrl',
        resolve: {
          content: () : IConfirmContent => {
            return {
              title: 'Cancel modifications',
              message: 'Are-you sure to want to cancel yours modifications?'
            }
          }
        }
      };
      this.$uibModal.open(options).result.then((pIsOk: boolean) => {
        this.$location.url("/");
      });
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
              type: ElementType.LINK,
              canEdit: true,
              canRemove: true
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
              type: ElementType.CONFIG,
              canEdit: false,
              canRemove: false
        });
        this.links = lLinks;
      })
    );
    this.$q.all(lPromises).then(
      () => {
        this.calculateButtonsAdd();
        this.isLoaded = true;
        this.init();
        var lHandler = () => {
          this.calculateProportions();
        };
        this.$scope.$on('$destroy', () => {
          angular.element(this.$window).unbind('resize', lHandler);
        });
        angular.element(this.$window).bind('resize', lHandler);
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

  private calculateButtonsAdd(): void {
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
          destinationY: j,
          canEdit: false,
          canRemove: false
        });
      }
    }
  }

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

    lScreenWidth -= 80;
    lScreenHeight -= 80;

    this.sizeSquareX = lScreenWidth / this.configuration.nbSquaresX;
    this.sizeSquareY = lScreenHeight / this.configuration.nbSquaresY;

    this.start();
  }

  private init(): void {
    this.calculateProportions();
  }

  private start = () => {
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
      var lPositionX = lDistanceX + this.centerX;
      var lDistanceY = lDestinationY - this.centerY;
      var lPositionY = lDistanceY + this.centerY;
      pLink.x = Math.round(lPositionX);
      pLink.y = Math.round(lPositionY);
    });
  }
}
