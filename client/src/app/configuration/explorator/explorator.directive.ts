import {ExploratorService, IExploratorObject, ExploratorType} from '../../../services/explorator';

interface IScope extends ng.IScope {
  ngModel: any;
  ngOnSelect: (pPath: string) => void;
}

export function ExploratorDirective(): ng.IDirective {
  return {
    controller: ExploratorDirectiveController,
    controllerAs: 'ctrl',
    templateUrl: 'app/configuration/explorator/explorator.directive.html',
    restrict: "E",
    scope: {
      ngModel: "=",
      ngOnSelect: "="
    }
  };
}

export interface IExploratorElement {
  name: string;
  path: string;
  subElements: IExploratorElement[];
  isLoaded: boolean;
  loadingInProgress: boolean;
  isFolder: boolean;
  isOpen: boolean;
}


export class ExploratorDirectiveController {
  public file: IExploratorElement = null;
  private onSelect: (pPath: string) => void = null;

  /* @ngInject */
  public constructor(
    private $scope: IScope,
    private ExploratorService: ExploratorService
  ) {
    if (!$scope.ngModel) {
      this.file = {
          name: null,
          path: null,
          subElements: [],
          isLoaded: false,
          loadingInProgress: false,
          isFolder: true,
          isOpen: true
      }
    } else {
      this.file = $scope.ngModel;
    }
    console.log($scope.ngOnSelect);
    this.onSelect = $scope.ngOnSelect;
    $scope.$watch('ctrl.file.isOpen', () => {
      this.open(this.file);
    });
    this.open(this.file);
  }

  public clickNode(pNode: IExploratorElement) {
    if (pNode.isFolder) {
      pNode.isOpen = !pNode.isOpen;
      return;
    }
    this.onSelect(pNode.path);
  }

  public open(pParent: IExploratorElement) {
    if (!this.file.isOpen) {
      return;
    }
    if (pParent.isLoaded) {
      return;
    }
    pParent.loadingInProgress = true;
    this.ExploratorService.get(pParent.path).then((pElements: IExploratorObject[]) => {
      var lSubElements: IExploratorElement[] = []
      pElements.forEach((pElement: IExploratorObject) => {
        lSubElements.push({
          name: pElement.name,
          path: pElement.path,
          subElements: [],
          isLoaded: false,
          loadingInProgress: false,
          isFolder: pElement.type === ExploratorType.FOLDER,
          isOpen: false
        });
      });
      pParent.subElements = lSubElements;
      pParent.isLoaded = true;
      pParent.loadingInProgress = false;
    });
  }
}
